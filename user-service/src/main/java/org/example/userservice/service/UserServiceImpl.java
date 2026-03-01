package org.example.userservice.service;

import org.example.userservice.domain.OrganizerTitle;
import org.example.userservice.domain.User;
import org.example.userservice.dto.UpdateProfileRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.dto.session.UpdateStatsRequest;
import org.example.userservice.dto.session.UserStatusResponse;
import org.example.userservice.messaging.NotifProducer;
import org.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import static io.micrometer.core.instrument.util.StringEscapeUtils.escapeJson;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final NotifProducer notifProducer;

    public UserServiceImpl(UserRepository userRepository, NotifProducer notifProducer){
        this.userRepository = userRepository;
        this.notifProducer = notifProducer;
    }

    @Override
    public UserResponse getMe(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse dto = new UserResponse();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setDateOfBirth(u.getDateOfBirth());
        dto.setRole("ROLE_" + u.getRole().name());
        dto.setActivated(u.isActivated());
        dto.setBlocked(u.isBlocked());

        dto.setSessionsReportedTotal(u.getSessionsReportedTotal());
        dto.setSessionsAttended(u.getSessionsAttended());
        dto.setSessionsMissed(u.getSessionsMissed());
        dto.setAttendancePercent(u.getAttendancePercent());
        dto.setSessionsOrganizedSuccessful(u.getSessionsOrganizedSuccessful());
        dto.setOrganizerTitle(u.getOrganizerTitle());

        return dto;
    }

    @Override
    public UserResponse updateMe(Long userId, UpdateProfileRequest request) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String newUsername = request.getUsername().trim();
            if (!newUsername.equals(u.getUsername()) && userRepository.existsByUsername(newUsername)) {
                throw new RuntimeException("Username already exists");
            }
            u.setUsername(newUsername);
        }


        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equals(u.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already exists");
            }
            u.setEmail(newEmail);
        }


        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            u.setFullName(request.getFullName().trim());
        }


        if (request.getDateOfBirth() != null) {
            u.setDateOfBirth(request.getDateOfBirth());
        }

        userRepository.save(u);
        return getMe(userId);
    }

    @Override
    public UserStatusResponse getUserStatus(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserStatusResponse dto = new UserStatusResponse();
        dto.setUserId(u.getId());
        dto.setActivated(u.isActivated());
        dto.setBlocked(u.isBlocked());
        dto.setRole("ROLE_" + u.getRole().name());
        dto.setAttendancePercent(u.getAttendancePercent());
        dto.setOrganizerTitle(u.getOrganizerTitle());

        return dto;
    }

    @Override
    public void updateStats(Long userId, UpdateStatsRequest request) {

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        u.setSessionsReportedTotal(u.getSessionsReportedTotal() + request.getReported());
        u.setSessionsAttended(u.getSessionsAttended() + request.getAttended());
        u.setSessionsMissed(u.getSessionsMissed() + request.getMissed());
        u.setSessionsOrganizedSuccessful(
                u.getSessionsOrganizedSuccessful() + request.getOrganized()
        );

        int total = u.getSessionsAttended() + u.getSessionsMissed();
        if (total > 0) {
            u.setAttendancePercent(
                    (u.getSessionsAttended() * 100.0) / total
            );
        }


        OrganizerTitle oldTitle = u.getOrganizerTitle();
        int x = u.getSessionsOrganizedSuccessful();
        OrganizerTitle newTitle;

        if (x >= 20) newTitle = OrganizerTitle.GOLD;
        else if (x >= 10) newTitle = OrganizerTitle.SILVER;
        else if (x >= 5) newTitle = OrganizerTitle.BRONZE;
        else newTitle = OrganizerTitle.NONE;

        u.setOrganizerTitle(newTitle);


        if (oldTitle != newTitle) {
            sendTitleChangeNotification(u, oldTitle, newTitle);
        }

        userRepository.save(u);
    }

    private void sendTitleChangeNotification(User user, OrganizerTitle oldTitle, OrganizerTitle newTitle) {
        String firstName = user.getFullName();
        if (firstName != null && firstName.contains(" ")) {
            firstName = firstName.split("\\s+")[0];
        }

        String msg = """
            {
              "type": "TITLE_CHANGE",
              "toEmail": "%s",
              "userId": %d,
              "correlationId": "title-change-%d",
              "vars": {
                "firstName": "%s",
                "oldTitle": "%s",
                "newTitle": "%s"
              }
            }
            """.formatted(
                user.getEmail(),
                user.getId(),
                user.getId(),
                escapeJson(firstName),
                oldTitle.name(),
                newTitle.name()
        );

        notifProducer.send(msg);
    }
    @Override
    public String getUserEmail(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return u.getEmail();
    }
}
