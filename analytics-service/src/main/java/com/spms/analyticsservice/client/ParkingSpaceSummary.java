package com.spms.analyticsservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpaceSummary {

    private Long id;
    private String zone;
    private String status; // "AVAILABLE" / "RESERVED" / "OCCUPIED"
    private Double zoneOccupancyRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getZoneOccupancyRate() {
        return zoneOccupancyRate;
    }

    public void setZoneOccupancyRate(Double zoneOccupancyRate) {
        this.zoneOccupancyRate = zoneOccupancyRate;
    }
}
