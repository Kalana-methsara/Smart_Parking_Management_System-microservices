package com.spms.notificationservice.dto;

import com.spms.notificationservice.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "type is required")
    private NotificationType type;

    @NotBlank(message = "message is required")
    private String message;

    /** Optional — which service triggered this, e.g. "payment-service". */
    private String sourceService;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }
}
