package com.spms.notificationservice.dto;

import com.spms.notificationservice.entity.Notification;
import com.spms.notificationservice.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private String sourceService;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUserId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.sourceService = notification.getSourceService();
        this.createdAt = notification.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceService() {
        return sourceService;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
