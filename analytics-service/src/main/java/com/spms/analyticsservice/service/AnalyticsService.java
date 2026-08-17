package com.spms.analyticsservice.service;

import com.spms.analyticsservice.client.ParkingServiceClient;
import com.spms.analyticsservice.client.ParkingSpaceSummary;
import com.spms.analyticsservice.client.PaymentServiceClient;
import com.spms.analyticsservice.client.PaymentSummary;
import com.spms.analyticsservice.dto.UsageAnalyticsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the live usage summary from two other services — nothing is
 * stored or cached here, so the numbers always reflect current state:
 *  - totalBookings: count of SUCCESS payments (a "booking" is treated as
 *    a completed, paid parking session)
 *  - mostUsedZone: the zone with the highest current occupancy rate
 *  - overallOccupancyRate: (RESERVED + OCCUPIED spaces) / total spaces,
 *    system-wide
 */
@Service
public class AnalyticsService {

    private final ParkingServiceClient parkingServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public AnalyticsService(ParkingServiceClient parkingServiceClient, PaymentServiceClient paymentServiceClient) {
        this.parkingServiceClient = parkingServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public UsageAnalyticsResponse getUsageAnalytics() {
        List<ParkingSpaceSummary> spaces = parkingServiceClient.getAllSpaces();
        List<PaymentSummary> payments = paymentServiceClient.getAllPayments();

        UsageAnalyticsResponse response = new UsageAnalyticsResponse();
        response.setGeneratedAt(LocalDateTime.now());

        // Total bookings = completed, successfully paid sessions.
        long totalBookings = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .count();
        response.setTotalBookings(totalBookings);

        response.setTotalParkingSpaces(spaces.size());

        if (spaces.isEmpty()) {
            response.setOverallOccupancyRate(0.0);
            response.setMostUsedZone(null);
            response.setOccupancyRateByZone(Map.of());
            return response;
        }

        // System-wide occupancy: RESERVED + OCCUPIED spaces / all spaces.
        long occupiedCount = spaces.stream()
                .filter(s -> "RESERVED".equals(s.getStatus()) || "OCCUPIED".equals(s.getStatus()))
                .count();
        response.setOverallOccupancyRate(round2((double) occupiedCount / spaces.size()));

        // Per-zone occupancy — each space already carries its zone's live
        // occupancy rate (computed by Parking Service), so just dedupe by zone.
        Map<String, Double> occupancyByZone = spaces.stream()
                .filter(s -> s.getZone() != null && s.getZoneOccupancyRate() != null)
                .collect(Collectors.toMap(
                        ParkingSpaceSummary::getZone,
                        s -> round2(s.getZoneOccupancyRate()),
                        (existing, duplicate) -> existing, // all spaces in a zone share the same rate
                        LinkedHashMap::new
                ));
        response.setOccupancyRateByZone(occupancyByZone);

        // Most-used zone = highest occupancy rate right now.
        String mostUsedZone = occupancyByZone.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        response.setMostUsedZone(mostUsedZone);

        return response;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
