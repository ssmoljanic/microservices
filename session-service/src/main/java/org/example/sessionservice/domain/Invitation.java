package org.example.sessionservice.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations", uniqueConstraints = @UniqueConstraint(columnNames = "token"))
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long invitedUserId;

    @Column(nullable = false)
    private boolean used = false;

    private LocalDateTime expiresAt;

    public Invitation() {}

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getInvitedUserId() { return invitedUserId; }
    public void setInvitedUserId(Long invitedUserId) { this.invitedUserId = invitedUserId; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
