package org.example.sessionservice.service.impl;

import org.example.sessionservice.domain.*;
import org.example.sessionservice.dto.CreateSessionRequest;
import org.example.sessionservice.dto.SessionResponse;
import org.example.sessionservice.dto.invitation.InviteResponse;
import org.example.sessionservice.dto.iternal.UpdateStatsRequest;
import org.example.sessionservice.dto.iternal.UserStatusResponse;
import org.example.sessionservice.messaging.NotifProducer;
import org.example.sessionservice.repository.GameRepository;
import org.example.sessionservice.repository.InvitationRepository;
import org.example.sessionservice.repository.SessionParticipantRepository;
import org.example.sessionservice.repository.SessionRepository;
import org.example.sessionservice.service.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.micrometer.core.instrument.util.StringEscapeUtils.escapeJson;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionParticipantRepository participantRepository;
    private final GameRepository gameRepository;
    private final InvitationRepository invitationRepository;
    private final RestTemplate restTemplate;
    private final NotifProducer notifProducer;

    @Value("${userservice.url}")
    private String userServiceUrl;


    public SessionServiceImpl(SessionRepository sessionRepository,
                              SessionParticipantRepository sessionParticipantRepository,
                              RestTemplate restTemplate,
                              GameRepository gameRepository,
                              InvitationRepository invitationRepository,
                              NotifProducer notifProducer) {
        this.sessionRepository = sessionRepository;
        this.participantRepository = sessionParticipantRepository;
        this.restTemplate = restTemplate;
        this.gameRepository = gameRepository;
        this.invitationRepository = invitationRepository;
        this.notifProducer = notifProducer;
    }

    @Override
    public SessionResponse create(Long organizerId, String authorization, CreateSessionRequest request) {

        if (request.getGameId() == null || !gameRepository.existsById(request.getGameId())) {
            throw new RuntimeException("gameId is invalid");
        }

        if (request.getSessionName() == null || request.getSessionName().isBlank()) {
            throw new RuntimeException("sessionName is required");
        }

        if (request.getMaxPlayers() <= 0) {
            throw new RuntimeException("maxPlayers must be > 0");
        }
        if (request.getStartTime() == null) {
            throw new RuntimeException("startTime is required");
        }
        if (request.getDurationMinutes() <= 0) {
            throw new RuntimeException("durationMinutes must be > 0");
        }

        UserStatusResponse us = fetchUserStatusWithRetry(organizerId, authorization);

        if (us == null) throw new RuntimeException("User not found");
        if (!us.isActivated()) throw new RuntimeException("User is not activated");
        if (us.isBlocked()) throw new RuntimeException("User is blocked");
        if (us.getAttendancePercent() < 90.0) throw new RuntimeException("Attendance percent < 90");

        Session s = new Session();
        s.setSessionName(request.getSessionName().trim());
        s.setDescription(request.getDescription());
        s.setMaxPlayers(request.getMaxPlayers());
        s.setStartTime(request.getStartTime());
        s.setDurationMinutes(request.getDurationMinutes());
        s.setOrganizerId(organizerId);
        s.setGameId(request.getGameId());
        s.setStatus(SessionStatus.CREATED);
        if (request.getType() != null) {
            s.setType(request.getType());
        }



        Session saved = sessionRepository.save(s);

        return toDto(saved);
    }

    @Override
    public SessionResponse getById(Long id) {
        Session s = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return toDto(s);
    }

    @Override
    public List<SessionResponse> getAll() {
        return sessionRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private SessionResponse toDto(Session s) {
        SessionResponse dto = new SessionResponse();
        dto.setId(s.getId());
        dto.setSessionName(s.getSessionName());
        dto.setGameId(s.getGameId());
        dto.setType(s.getType());
        dto.setDescription(s.getDescription());
        dto.setMaxPlayers(s.getMaxPlayers());
        dto.setStartTime(s.getStartTime());
        dto.setDurationMinutes(s.getDurationMinutes());
        dto.setStatus(s.getStatus());
        dto.setOrganizerId(s.getOrganizerId());
        return dto;
    }


    @Override
    public void join(Long sessionId, Long userId, String authorization) {

        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (s.getStatus() != SessionStatus.CREATED) {
            throw new RuntimeException("Session is not open for joining");
        }


        if (s.getType() == SessionType.CLOSED) {
            throw new RuntimeException("CLOSED sessions require invitation");
        }


        if (participantRepository.findBySessionIdAndUserId(sessionId, userId).isPresent()) {
            throw new RuntimeException("Already joined");
        }


        long count = participantRepository.countBySessionId(sessionId);
        if (count >= s.getMaxPlayers()) {
            throw new RuntimeException("Session is full");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserStatusResponse> resp = restTemplate.exchange(
                userServiceUrl + "/internal/users/" + userId,
                HttpMethod.GET,
                entity,
                UserStatusResponse.class
        );

        UserStatusResponse us = resp.getBody();
        if (us == null || !us.isActivated() || us.isBlocked()) {
            throw new RuntimeException("User is not allowed");
        }
        updateUserStatsWithRetry(userId, authorization, new UpdateStatsRequest(1, 0, 0, 0));



        SessionParticipant p = new SessionParticipant();
        p.setSessionId(sessionId);
        p.setUserId(userId);
        p.setStatus(ParticipantStatus.SIGNED_UP);

        participantRepository.save(p);
    }

    @Override
    public void leave(Long sessionId, Long userId, String authorization) {
        SessionParticipant p = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Not joined"));

        participantRepository.delete(p);


        updateUserStatsWithRetry(userId, authorization, new UpdateStatsRequest(-1, 0, 0, 0));
    }

    @Override
    public void finish(Long sessionId, Long requesterId, String authorization, List<Long> attendedUserIds) {

        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));


        if (s.getStatus() != SessionStatus.CREATED) {
            throw new RuntimeException("Only CREATED sessions can be finished");
        }


        if (!s.getOrganizerId().equals(requesterId)) {
            throw new RuntimeException("Only organizer can finish the session");
        }


        List<Long> participantIds = participantRepository.findAllBySessionId(sessionId).stream()
                .map(SessionParticipant::getUserId)
                .distinct()
                .filter(id -> !id.equals(s.getOrganizerId()))
                .toList();


        if (participantIds.isEmpty()) {
            throw new RuntimeException("Session cannot be finished without participants");
        }

        if (attendedUserIds == null) attendedUserIds = List.of();


        if (attendedUserIds.contains(s.getOrganizerId())) {
            throw new RuntimeException("Organizer cannot be in attendedUserIds");
        }


        Set<Long> participantSet = new HashSet<>(participantIds);
        List<Long> invalid = attendedUserIds.stream()
                .filter(id -> !participantSet.contains(id))
                .distinct()
                .toList();

        if (!invalid.isEmpty()) {
            throw new RuntimeException("Attended list contains users not joined to the session: " + invalid);
        }


        Set<Long> attendedSet = new HashSet<>(attendedUserIds);
        if (attendedSet.isEmpty()) {
            throw new RuntimeException("Session cannot be finished without at least one attended player (besides organizer)");
        }


        for (Long pid : participantIds) {
            boolean attended = attendedSet.contains(pid);

            UpdateStatsRequest req = new UpdateStatsRequest();
            req.setReported(0);
            req.setAttended(attended ? 1 : 0);
            req.setMissed(attended ? 0 : 1);
            req.setOrganized(0);

            updateUserStatsWithRetry(pid, authorization, req);
        }


        UpdateStatsRequest orgReq = new UpdateStatsRequest();
        orgReq.setReported(0);
        orgReq.setAttended(0);
        orgReq.setMissed(0);
        orgReq.setOrganized(1);

        updateUserStatsWithRetry(s.getOrganizerId(), authorization, orgReq);


        s.setStatus(SessionStatus.FINISHED);
        sessionRepository.save(s);
    }


    @Override
    public void cancel(Long sessionId, Long requesterId, String requesterRole) {

        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));


        if (s.getStatus() != SessionStatus.CREATED) {
            throw new RuntimeException("Only CREATED sessions can be canceled");
        }

        boolean isAdmin = "ROLE_ADMIN".equals(requesterRole);
        boolean isOrganizer = s.getOrganizerId().equals(requesterId);

        if (!isAdmin && !isOrganizer) {
            throw new RuntimeException("Only organizer or admin can cancel");
        }


        s.setStatus(SessionStatus.CANCELED);
        sessionRepository.save(s);


        List<Long> participantUserIds = participantRepository.findAllBySessionId(sessionId)
                .stream()
                .map(SessionParticipant::getUserId)
                .distinct()
                .toList();

        for (Long uid : participantUserIds) {
            String correlationId = "cancel-" + sessionId + "-" + uid;

            String msg = """
    {
      "type":"SESSION_CANCELLED",
      "toEmail": null,
      "userId": %d,
      "correlationId":"%s",
      "vars":{
        "sessionName":"%s",
        "reason":"%s"
      }
    }
    """.formatted(
                    uid,
                    correlationId,
                    escapeJson(s.getSessionName()),
                    "Organizer cancelled"
            );

            notifProducer.send(msg);
        }
    }


    @Override
    public InviteResponse invite(Long sessionId, Long requesterId, Long invitedUserId) {

        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));


        if (s.getType() != SessionType.CLOSED) {
            throw new RuntimeException("Invites are only for CLOSED sessions");
        }


        if (s.getStatus() != SessionStatus.CREATED) {
            throw new RuntimeException("Session is not open for inviting");
        }


        if (!s.getOrganizerId().equals(requesterId)) {
            throw new RuntimeException("Only organizer can invite");
        }


        if (invitationRepository.existsBySessionIdAndInvitedUserIdAndUsedFalse(sessionId, invitedUserId)) {
            throw new RuntimeException("Invitation already exists");
        }


        String token = java.util.UUID.randomUUID().toString();


        Invitation inv = new Invitation();
        inv.setToken(token);
        inv.setSessionId(sessionId);
        inv.setInvitedUserId(invitedUserId);
        inv.setUsed(false);
        // inv.setExpiresAt(LocalDateTime.now().plusDays(2));
        invitationRepository.save(inv);


        String correlationId = "invite-" + sessionId + "-" + invitedUserId;

        String sessionLink = "http://localhost:5173/invitation/accept?token=" + token;

        String msg = """
{
  "type": "SESSION_INVITE",
  "toEmail": null,
  "userId": %d,
  "correlationId": "%s",
  "vars": {
    "sessionName": "%s",
    "startTime": "%s",
    "sessionLink": "%s"
  }
}
""".formatted(
                invitedUserId,
                correlationId,
                escapeJson(s.getSessionName()),
                s.getStartTime().toString(),
                escapeJson(sessionLink)
        );


        notifProducer.send(msg);
        return new InviteResponse(token);
    }

    @Override
    public void acceptInvitation(String token, Long userId, String authorization) {


        Invitation inv = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (inv.isUsed()) {
            throw new RuntimeException("Invitation already used");
        }


        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Invitation expired");
        }


        if (!inv.getInvitedUserId().equals(userId)) {
            throw new RuntimeException("Invitation does not belong to this user");
        }


        Session s = sessionRepository.findById(inv.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (s.getStatus() != SessionStatus.CREATED) {
            throw new RuntimeException("Session is not open for joining");
        }
        if (s.getType() != SessionType.CLOSED) {
            throw new RuntimeException("Invitation is only for CLOSED sessions");
        }


        if (participantRepository.findBySessionIdAndUserId(s.getId(), userId).isPresent()) {
            throw new RuntimeException("Already joined");
        }


        long count = participantRepository.countBySessionId(s.getId());
        if (count >= s.getMaxPlayers()) {
            throw new RuntimeException("Session is full");
        }


        UserStatusResponse us = fetchUserStatusWithRetry(userId, authorization);

        if (us == null) throw new RuntimeException("User not found");
        if (!us.isActivated()) throw new RuntimeException("User is not activated");
        if (us.isBlocked()) throw new RuntimeException("User is blocked");


        updateUserStatsWithRetry(userId, authorization, new UpdateStatsRequest(1, 0, 0, 0));


        SessionParticipant p = new SessionParticipant();
        p.setSessionId(s.getId());
        p.setUserId(userId);
        p.setStatus(ParticipantStatus.SIGNED_UP);

        participantRepository.save(p);


        inv.setUsed(true);
        invitationRepository.save(inv);
    }



    @Override
    public List<SessionResponse> search(Long gameId,
                                        SessionType type,
                                        Integer maxPlayers,
                                        String keyword,
                                        boolean joined,
                                        Long userId,
                                        String sortBy,
                                        String direction) {

        List<Session> sessions = sessionRepository.findAll();


        sessions = sessions.stream()
                .filter(s -> s.getStatus() == SessionStatus.CREATED)
                .toList();

        if (gameId != null) {
            sessions = sessions.stream()
                    .filter(s -> s.getGameId().equals(gameId))
                    .toList();
        }

        if (type != null) {
            sessions = sessions.stream()
                    .filter(s -> s.getType() == type)
                    .toList();
        }

        if (maxPlayers != null) {
            sessions = sessions.stream()
                    .filter(s -> s.getMaxPlayers() <= maxPlayers)
                    .toList();
        }

        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            sessions = sessions.stream()
                    .filter(s ->
                            (s.getDescription() != null && s.getDescription().toLowerCase().contains(k)) ||
                                    (s.getSessionName() != null && s.getSessionName().toLowerCase().contains(k))
                    )
                    .toList();
        }

        if (joined) {
            var joinedIds = participantRepository.findAllByUserId(userId).stream()
                    .map(SessionParticipant::getSessionId)
                    .collect(java.util.stream.Collectors.toSet());

            sessions = sessions.stream()
                    .filter(s -> joinedIds.contains(s.getId()))
                    .toList();
        }

        List<SessionResponse> out = sessions.stream()
                .map(s -> SessionResponse.from(s, participantRepository.countBySessionId(s.getId())))
                .toList();

        java.util.Comparator<SessionResponse> cmp =
                "playersCount".equalsIgnoreCase(sortBy)
                        ? java.util.Comparator.comparingLong(SessionResponse::getPlayersCount)
                        : java.util.Comparator.comparing(SessionResponse::getStartTime);

        if ("desc".equalsIgnoreCase(direction)) cmp = cmp.reversed();

        return out.stream().sorted(cmp).toList();
    }


    //  Retry
    private UserStatusResponse fetchUserStatusWithRetry(Long userId, String authorization) {

        int attempts = 3;
        long delayMs = 300;

        for (int i = 1; i <= attempts; i++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authorization);

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<UserStatusResponse> resp = restTemplate.exchange(
                        userServiceUrl + "/internal/users/" + userId,
                        HttpMethod.GET,
                        entity,
                        UserStatusResponse.class
                );

                return resp.getBody();

            } catch (Exception ex) {
                if (i == attempts) {
                    throw new RuntimeException("UserService unavailable");
                }
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
                delayMs *= 2; // 300 -> 600 -> 1200
            }
        }
        throw new RuntimeException("UserService unavailable");
    }

    private void updateUserStatsWithRetry(Long userId, String authorization, UpdateStatsRequest request) {
        int attempts = 3;
        long delayMs = 300;

        for (int i = 1; i <= attempts; i++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authorization);

                HttpEntity<UpdateStatsRequest> entity = new HttpEntity<>(request, headers);

                restTemplate.exchange(
                        userServiceUrl + "/internal/users/" + userId + "/stats",
                        HttpMethod.POST,
                        entity,
                        Void.class
                );
                return;
            } catch (Exception ex) {
                if (i == attempts) throw new RuntimeException("UserService unavailable (stats)");
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
                delayMs *= 2;
            }
        }
    }


}
