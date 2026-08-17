package com.spms.parkingservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Notifies Notification Service when a reservation is confirmed. This is
 * best-effort: if Notification Service is down, the reservation itself
 * must still succeed — a notification failure should never roll back a
 * real booking.
 */
@Component
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
    private static final String NOTIFICATIONS_URL = "http://notification-service/notifications";

    private final RestTemplate restTemplate;

    public NotificationServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyBookingConfirmed(Long userId, String message) {
        if (userId == null) {
            return;
        }
        try {
            Map<String, Object> body = Map.of(
                    "userId", userId,
                    "type", "BOOKING_CONFIRMED",
                    "message", message,
                    "sourceService", "parking-service"
            );
            restTemplate.postForLocation(NOTIFICATIONS_URL, body);
        } catch (RestClientException ex) {
            log.warn("Could not send booking-confirmed notification for user {}: {}", userId, ex.getMessage());
        }
    }
}
