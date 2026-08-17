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

@Component
public class PaymentServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceClient.class);
    private static final String PAYMENTS_URL = "http://payment-service/payments";

    private final RestTemplate restTemplate;

    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PaymentSummary> getAllPayments() {
        try {
            ResponseEntity<List<PaymentSummary>> response = restTemplate.exchange(
                    PAYMENTS_URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<PaymentSummary>>() {});
            List<PaymentSummary> payments = response.getBody();
            return payments != null ? payments : Collections.emptyList();
        } catch (RestClientException ex) {
            log.warn("Could not fetch payments for analytics: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
