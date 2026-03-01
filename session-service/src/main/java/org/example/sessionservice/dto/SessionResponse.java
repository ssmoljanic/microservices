package org.example.sessionservice.dto;

import org.example.sessionservice.domain.Session;
import org.example.sessionservice.domain.SessionStatus;
import org.example.sessionservice.domain.SessionType;

import java.time.LocalDateTime;

public class SessionResponse {
    private Long id;
    private String sessionName;
    private int maxPlayers;
    private long playersCount;

    private LocalDateTime startTime;
    private int durationMinutes;
    private String description;

    private SessionStatus status;
    private SessionType type;

    private Long organizerId;
    private LocalDateTime createdAt;
    private Long gameId;

    public static SessionResponse from(Session s, long playersCount) {
        SessionResponse r = new SessionResponse();
        r.id = s.getId();
        r.sessionName = s.getSessionName();
        r.maxPlayers = s.getMaxPlayers();
        r.playersCount = playersCount;

        r.startTime = s.getStartTime();
        r.durationMinutes = s.getDurationMinutes();
        r.description = s.getDescription();

        r.status = s.getStatus();
        r.type = s.getType();

        r.organizerId = s.getOrganizerId();
        r.createdAt = s.getCreatedAt();
        r.gameId = s.getGameId();
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

    public Long getGameId() {return gameId;}

    public void setGameId(Long gameId) {this.gameId = gameId;}

    public SessionType getType() {return type;}

    public void setType(SessionType type) {this.type = type;}

    public String getSessionName() {return sessionName;}

    public void setSessionName(String sessionName) {this.sessionName = sessionName;}

    public long getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(long playersCount) {
        this.playersCount = playersCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
