package com.spms.notificationservice.service;

import com.spms.notificationservice.dto.NotificationRequest;
import com.spms.notificationservice.dto.NotificationResponse;
import com.spms.notificationservice.entity.Notification;
import com.spms.notificationservice.exception.ResourceNotFoundException;
import com.spms.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * "Sends" a notification. No real email/SMS/push provider is wired up —
     * this simulation logs the event (as a real dispatch log would) and
     * persists it so it can be reviewed via GET /notifications.
     */
    public NotificationResponse send(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setSourceService(request.getSourceService());

        Notification saved = notificationRepository.save(notification);

        log.info("[NOTIFY] to user {} ({}): {}{}",
                saved.getUserId(), saved.getType(), saved.getMessage(),
                saved.getSourceService() != null ? " [from " + saved.getSourceService() + "]" : "");

        return new NotificationResponse(saved);
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    public NotificationResponse getById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return new NotificationResponse(notification);
    }
}
