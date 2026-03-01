package rs.raf.notificationservis.dto;

import java.time.Instant;

public class NotificationResponse {

    private Long id;
    private String typeCode;
    private String recipientEmail;
    private String status;
    private String correlationId;
    private String subject;
    private Instant createdAt;
    private Instant sentAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String typeCode, String recipientEmail, String status,
                                String correlationId, String subject, Instant createdAt, Instant sentAt) {
        this.id = id;
        this.typeCode = typeCode;
        this.recipientEmail = recipientEmail;
        this.status = status;
        this.correlationId = correlationId;
        this.subject = subject;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
