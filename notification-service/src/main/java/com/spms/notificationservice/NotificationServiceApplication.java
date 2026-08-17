package com.spms.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Notification Service - simulates sending notifications to users and
 * logs every one for later review.
 *
 * No real email/SMS/push gateway is involved (this is a simulation for
 * the assignment's scope) — "sending" a notification means logging it via
 * SLF4J and persisting it so it can be queried back later.
 *
 * Other services (Payment Service on successful payment, Parking Service
 * on successful reservation) call this service to log events like
 * "Booking confirmed" and "Payment success".
 *
 * Registers with Eureka as "NOTIFICATION-SERVICE" and pulls its config
 * from the Config Server.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
