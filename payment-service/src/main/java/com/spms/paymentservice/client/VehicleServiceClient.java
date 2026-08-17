package com.spms.paymentservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class VehicleServiceClient {

    private static final Logger log = LoggerFactory.getLogger(VehicleServiceClient.class);
    private static final String VEHICLE_URL = "http://vehicle-service/vehicles/{id}";
    private static final String VEHICLE_LOGS_URL = "http://vehicle-service/vehicles/{id}/logs";

    private final RestTemplate restTemplate;

    public VehicleServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<VehicleInfo> getVehicle(Long vehicleId) {
        if (vehicleId == null) {
            return Optional.empty();
        }
        try {
            VehicleInfo vehicle = restTemplate.getForObject(VEHICLE_URL, VehicleInfo.class, vehicleId);
            return Optional.ofNullable(vehicle);
        } catch (RestClientException ex) {
            log.warn("Could not fetch vehicle {} for receipt: {}", vehicleId, ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<VehicleLogInfo> getLatestCompletedLog(Long vehicleId) {
        if (vehicleId == null) {
            return Optional.empty();
        }
        try {
            ResponseEntity<List<VehicleLogInfo>> response = restTemplate.exchange(
                    VEHICLE_LOGS_URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<VehicleLogInfo>>() {}, vehicleId);

            List<VehicleLogInfo> logs = response.getBody();
            if (logs == null) {
                return Optional.empty();
            }

            return logs.stream()
                    .filter(l -> "COMPLETED".equals(l.getStatus()) && l.getDurationMinutes() != null)
                    .max(Comparator.comparing(VehicleLogInfo::getId));
        } catch (RestClientException ex) {
            log.warn("Could not fetch logs for vehicle {} for receipt: {}", vehicleId, ex.getMessage());
            return Optional.empty();
        }
    }
}
