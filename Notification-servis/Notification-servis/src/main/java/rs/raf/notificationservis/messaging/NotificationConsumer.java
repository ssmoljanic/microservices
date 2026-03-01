package rs.raf.notificationservis.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import rs.raf.notificationservis.dto.NotificationMessage;
import rs.raf.notificationservis.repository.NotificationLogRepository;
import rs.raf.notificationservis.service.NotificationService;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;

    public NotificationConsumer(ObjectMapper objectMapper,
                                NotificationService notificationService,
                                NotificationLogRepository notificationLogRepository) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.notificationLogRepository = notificationLogRepository;
    }

    @JmsListener(destination = "${app.jms.notification-queue}")
    public void onMessage(String messageJson) {
        NotificationMessage msg;

        try {
            msg = objectMapper.readValue(messageJson, NotificationMessage.class);
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON. Cannot parse message.", e);
            return;
        }

        if (isBlank(msg.getType()) || isBlank(msg.getCorrelationId())) {
            log.warn("Missing required fields (type/correlationId). Skipping. Parsed={}", safeToString(msg));
            return;
        }


        if (notificationLogRepository.findByCorrelationId(msg.getCorrelationId()).isPresent()) {
            log.info("Duplicate correlationId={}, skipping.", msg.getCorrelationId());
            return;
        }


        notificationService.handleIncoming(msg, messageJson);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safeToString(NotificationMessage msg) {
        if (msg == null) return "null";
        return "NotificationMessage{type=" + msg.getType() +
                ", toEmail=" + msg.getToEmail() +
                ", userId=" + msg.getUserId() +
                ", correlationId=" + msg.getCorrelationId() + "}";
    }
}