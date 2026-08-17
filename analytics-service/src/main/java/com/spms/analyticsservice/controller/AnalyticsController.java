package com.spms.analyticsservice.controller;

import com.spms.analyticsservice.dto.UsageAnalyticsResponse;
import com.spms.analyticsservice.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET /analytics/usage — total bookings, most-used zone, occupancy rate
    @GetMapping("/usage")
    public ResponseEntity<UsageAnalyticsResponse> getUsageAnalytics() {
        return ResponseEntity.ok(analyticsService.getUsageAnalytics());
    }
}
