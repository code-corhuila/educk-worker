package com.corhuila.edutrack.worker.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID userId;
    private String type; // ACADEMIC, ATTENDANCE, SYSTEM
    private String title;
    private String message;
    private String channel; // EMAIL, SMS, PUSH, IN_APP
    private String status;  // SENT, DELIVERED, FAILED
    private LocalDateTime createdAt;

    public Notification(UUID id, UUID userId, String type, String title, String message, String channel, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.channel = channel;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getChannel() { return channel; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
