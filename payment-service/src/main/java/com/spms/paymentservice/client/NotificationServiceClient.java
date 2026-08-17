package com.spms.paymentservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
    private static final String NOTIFICATIONS_URL = "http://notification-service/notifications";

    private final RestTemplate restTemplate;

    public NotificationServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyPaymentResult(Long userId, boolean success, String message) {
        if (userId == null) {
            return;
        }
        try {
            Map<String, Object> body = Map.of(
                    "userId", userId,
                    "type", success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED",
                    "message", message,
                    "sourceService", "payment-service"
            );
            restTemplate.postForLocation(NOTIFICATIONS_URL, body);
        } catch (RestClientException ex) {
            log.warn("Could not send payment notification for user {}: {}", userId, ex.getMessage());
        }
    }
}
