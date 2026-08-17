package com.spms.paymentservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Fetches user details for receipts. Returns Optional.empty() (rather than
 * throwing) if User Service is unreachable or the user isn't found — a
 * receipt should still render with "N/A" fields instead of failing
 * entirely just because one downstream call had trouble.
 */
@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private static final String USER_URL = "http://user-service/users/{id}";

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<UserInfo> getUser(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        try {
            UserInfo user = restTemplate.getForObject(USER_URL, UserInfo.class, userId);
            return Optional.ofNullable(user);
        } catch (RestClientException ex) {
            log.warn("Could not fetch user {} for receipt: {}", userId, ex.getMessage());
            return Optional.empty();
        }
    }
}
