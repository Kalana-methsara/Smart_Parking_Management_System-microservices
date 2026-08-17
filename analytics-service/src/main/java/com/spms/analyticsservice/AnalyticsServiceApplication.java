package com.spms.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Analytics Service - aggregates live usage data across SPMS for
 * dashboards/reporting.
 *
 * Business logic owned by this service:
 *  - Total completed bookings (successful payments)
 *  - Most-used parking zone (by current occupancy)
 *  - System-wide occupancy rate
 *
 * This service is stateless — it holds no database of its own and simply
 * queries Parking Service and Payment Service (over Eureka) on demand,
 * then aggregates the results. Registers with Eureka as "ANALYTICS-SERVICE".
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
