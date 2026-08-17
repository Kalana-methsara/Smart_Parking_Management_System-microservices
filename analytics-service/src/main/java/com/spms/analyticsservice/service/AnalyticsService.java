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

        long occupiedCount = spaces.stream()
                .filter(s -> "RESERVED".equals(s.getStatus()) || "OCCUPIED".equals(s.getStatus()))
                .count();
        response.setOverallOccupancyRate(round2((double) occupiedCount / spaces.size()));


        Map<String, Double> occupancyByZone = spaces.stream()
                .filter(s -> s.getZone() != null && s.getZoneOccupancyRate() != null)
                .collect(Collectors.toMap(
                        ParkingSpaceSummary::getZone,
                        s -> round2(s.getZoneOccupancyRate()),
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ));
        response.setOccupancyRateByZone(occupancyByZone);

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
