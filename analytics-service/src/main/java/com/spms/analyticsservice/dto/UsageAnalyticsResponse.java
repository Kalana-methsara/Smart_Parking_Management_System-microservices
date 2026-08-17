package com.spms.analyticsservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class UsageAnalyticsResponse {

    private long totalBookings;
    private String mostUsedZone;
    private double overallOccupancyRate;
    private long totalParkingSpaces;
    private Map<String, Double> occupancyRateByZone;
    private LocalDateTime generatedAt;

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public String getMostUsedZone() {
        return mostUsedZone;
    }

    public void setMostUsedZone(String mostUsedZone) {
        this.mostUsedZone = mostUsedZone;
    }

    public double getOverallOccupancyRate() {
        return overallOccupancyRate;
    }

    public void setOverallOccupancyRate(double overallOccupancyRate) {
        this.overallOccupancyRate = overallOccupancyRate;
    }

    public long getTotalParkingSpaces() {
        return totalParkingSpaces;
    }

    public void setTotalParkingSpaces(long totalParkingSpaces) {
        this.totalParkingSpaces = totalParkingSpaces;
    }

    public Map<String, Double> getOccupancyRateByZone() {
        return occupancyRateByZone;
    }

    public void setOccupancyRateByZone(Map<String, Double> occupancyRateByZone) {
        this.occupancyRateByZone = occupancyRateByZone;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
