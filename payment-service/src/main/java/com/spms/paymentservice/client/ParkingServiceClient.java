package com.spms.paymentservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class ParkingServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ParkingServiceClient.class);
    private static final String SPACE_URL = "http://parking-service/spaces/{id}";
    private static final String RELEASE_URL = "http://parking-service/spaces/{id}/release";

    private final RestTemplate restTemplate;

    public ParkingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ParkingSpaceInfo> getSpace(Long spaceId) {
        if (spaceId == null) {
            return Optional.empty();
        }
        try {
            ParkingSpaceInfo space = restTemplate.getForObject(SPACE_URL, ParkingSpaceInfo.class, spaceId);
            return Optional.ofNullable(space);
        } catch (RestClientException ex) {
            log.warn("Could not fetch parking space {} for receipt: {}", spaceId, ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Day 18 integration: once a parking fee is paid successfully, release
     * the space back to AVAILABLE — closing the loop that started with
     * reserveSpace() in Parking Service. Best-effort: if Parking Service is
     * unreachable or the space is already available (409), the payment
     * result itself is unaffected.
     */
    public void releaseSpaceAfterPayment(Long spaceId) {
        if (spaceId == null) {
            return;
        }
        try {
            restTemplate.put(RELEASE_URL, null, spaceId);
            log.info("Released parking space {} after successful payment", spaceId);
        } catch (RestClientException ex) {
            log.warn("Could not release parking space {} after payment: {}", spaceId, ex.getMessage());
        }
    }
}
