package rs.raf.notificationservis.service;

import rs.raf.notificationservis.dto.NotificationMessage;

public interface NotificationService {
    void handleIncoming(NotificationMessage msg, String rawJson);
}
