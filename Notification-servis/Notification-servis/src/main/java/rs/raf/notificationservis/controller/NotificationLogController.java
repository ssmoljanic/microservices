package rs.raf.notificationservis.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import rs.raf.notificationservis.domain.NotificationLog;
import rs.raf.notificationservis.dto.NotificationResponse;
import rs.raf.notificationservis.repository.NotificationLogRepository;
import rs.raf.notificationservis.security.CheckSecurity;
import rs.raf.notificationservis.security.service.TokenService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationLogController {

    private final NotificationLogRepository repo;
    private final TokenService tokenService;

    public NotificationLogController(NotificationLogRepository repo, TokenService tokenService) {
        this.repo = repo;
        this.tokenService = tokenService;
    }

    @GetMapping
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public Page<NotificationResponse> all(
            @RequestHeader("Authorization") String authorization,
            Pageable pageable
    ) {
        return repo.findAll(pageable).map(this::toDto);
    }

    @GetMapping("/me")
    @CheckSecurity(roles = {"ROLE_PLAYER"})
    public Page<NotificationResponse> mine(
            @RequestHeader("Authorization") String authorization,
            Pageable pageable
    ) {
        String token = authorization.split(" ")[1];
        Long userId = tokenService.getUserId(token);

        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }

        return repo.findAllByRecipientUserId(userId, pageable)
                .map(this::toDto);
    }




    private NotificationResponse toDto(NotificationLog n) {
        return new NotificationResponse(
                n.getId(),
                n.getTypeCode(),
                n.getRecipientEmail(),
                n.getStatus(),
                n.getCorrelationId(),
                n.getSubject(),
                n.getCreatedAt(),
                n.getSentAt()
        );
    }
}