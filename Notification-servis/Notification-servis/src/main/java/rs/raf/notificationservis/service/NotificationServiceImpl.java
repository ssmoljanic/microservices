package rs.raf.notificationservis.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.raf.notificationservis.domain.NotificationLog;
import rs.raf.notificationservis.domain.NotificationType;
import rs.raf.notificationservis.domain.UserClient;
import rs.raf.notificationservis.dto.NotificationMessage;
import rs.raf.notificationservis.dto.UserEmailResponse;
import rs.raf.notificationservis.mail.EmailSender;
import rs.raf.notificationservis.repository.NotificationLogRepository;
import rs.raf.notificationservis.repository.NotificationTypeRepository;
import rs.raf.notificationservis.security.service.TokenService;
import rs.raf.notificationservis.service.NotificationService;
import rs.raf.notificationservis.util.TemplateRenderer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationTypeRepository typeRepo;
    private final NotificationLogRepository logRepo;
    private final TemplateRenderer renderer;
    private final EmailSender emailSender;
    private final UserClient userClient;
    private final TokenService tokenService;

    public NotificationServiceImpl(NotificationTypeRepository typeRepo, NotificationLogRepository logRepo, TemplateRenderer renderer, EmailSender emailSender, UserClient userClient, TokenService tokenService) {
        this.typeRepo = typeRepo;
        this.logRepo = logRepo;
        this.renderer = renderer;
        this.emailSender = emailSender;
        this.userClient = userClient;
        this.tokenService = tokenService;
    }

    @Override
    public void handleIncoming(NotificationMessage msg, String rawJson) {

        NotificationType type = typeRepo.findByCode(msg.getType())
                .orElseThrow(() -> new IllegalArgumentException("Unknown notification type: " + msg.getType()));


        String recipientEmail = msg.getToEmail();

        if (recipientEmail == null || recipientEmail.isBlank()) {
            if (msg.getUserId() == null) {
                throw new IllegalArgumentException("Missing recipient: neither toEmail nor userId.");
            }

            String serviceJwt = tokenService.generateServiceToken();
            String authorization = "Bearer " + serviceJwt;

            var u = userClient.getEmail(msg.getUserId(), authorization);

            if (u == null || u.getEmail() == null || u.getEmail().isBlank()) {
                throw new IllegalArgumentException("Cannot resolve email for userId=" + msg.getUserId());
            }

            recipientEmail = u.getEmail().trim();
        }


        var vars = msg.getVars();
        if (vars == null) vars = new HashMap<>();


        String subject = renderer.render(type.getSubjectTemplate(), vars);
        String body = renderer.render(type.getBodyTemplate(), vars);


        NotificationLog logEntity = new NotificationLog();
        logEntity.setTypeCode(msg.getType());
        logEntity.setRecipientEmail(recipientEmail);
        logEntity.setRecipientUserId(msg.getUserId());
        logEntity.setCorrelationId(msg.getCorrelationId());
        logEntity.setPayloadJson(rawJson);
        logEntity.setStatus("GENERATED");
        logEntity.setSubject(subject);
        logEntity.setBody(body);

        logRepo.save(logEntity);

        try {
            emailSender.send(recipientEmail, subject, body);

            logEntity.setStatus("SENT");
            logEntity.setSentAt(Instant.now());
            logEntity.setErrorMessage(null);

        } catch (Exception e) {
            logEntity.setStatus("FAILED");
            logEntity.setSentAt(null);

            String message = e.getMessage();
            if (message != null && message.length() > 500) message = message.substring(0, 500);
            logEntity.setErrorMessage(message);
        }

        logRepo.save(logEntity);
        log.info("Notification processed, status={}, correlationId={}", logEntity.getStatus(), msg.getCorrelationId());
    }
}

/*

{
        "type": "ACCOUNT_ACTIVATION",
        "toEmail": "bdjurovic9224rn@raf.rs",
        "userId": 1,
        "correlationId": "c17",
        "vars": {
        "firstName": "Bogdan",
        "activationLink": "http://example.com/activate"
        }
        } */
