package com.spms.analyticsservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Fetches the full parking space list for analytics. Returns an empty list
 * (rather than throwing) if Parking Service is unreachable, so the
 * analytics endpoint degrades gracefully instead of failing outright.
 */
@Component
public class ParkingServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ParkingServiceClient.class);
    private static final String SPACES_URL = "http://parking-service/spaces";

    private final RestTemplate restTemplate;

    public ParkingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ParkingSpaceSummary> getAllSpaces() {
        try {
            ResponseEntity<List<ParkingSpaceSummary>> response = restTemplate.exchange(
                    SPACES_URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<ParkingSpaceSummary>>() {});
            List<ParkingSpaceSummary> spaces = response.getBody();
            return spaces != null ? spaces : Collections.emptyList();
        } catch (RestClientException ex) {
            log.warn("Could not fetch parking spaces for analytics: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
