package com.spms.vehicleservice.client;

import com.spms.vehicleservice.exception.ExternalServiceException;
import com.spms.vehicleservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private static final String USER_SERVICE_URL = "http://user-service/users/{id}";

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void verifyUserExists(Long userId) {
        try {
            restTemplate.getForEntity(USER_SERVICE_URL, String.class, userId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        } catch (ResourceAccessException | HttpClientErrorException ex) {
            throw new ExternalServiceException(
                    "Could not verify user " + userId + " — User Service is unreachable or returned an error");
        }
    }
}
