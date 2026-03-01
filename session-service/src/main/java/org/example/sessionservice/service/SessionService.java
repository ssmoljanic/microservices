package org.example.sessionservice.service;

import org.example.sessionservice.domain.SessionType;
import org.example.sessionservice.dto.CreateSessionRequest;
import org.example.sessionservice.dto.SessionResponse;
import org.example.sessionservice.dto.invitation.InviteResponse;

import java.util.List;

public interface SessionService {

    SessionResponse create(Long organizerId, String authorization, CreateSessionRequest request);

    SessionResponse getById(Long id);

    List<SessionResponse> getAll();

    void join(Long sessionId, Long userId, String authorization);

    void leave(Long sessionId, Long userId, String authorization);

    void finish(Long sessionId, Long requesterId, String authorization, List<Long> attendedUserIds);

    public List<SessionResponse> search(Long gameId,
                                        SessionType type,
                                        Integer maxPlayers,
                                        String keyword,
                                        boolean joined,
                                        Long userId,
                                        String sortBy,
                                        String direction);

    void cancel(Long sessionId, Long userId, String requesterRole);

    InviteResponse invite(Long sessionId, Long requesterId, Long invitedUserId);

    void acceptInvitation(String token, Long userId, String authorization);



}
