package com.spms.paymentservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Mirrors just the fields of UserResponse (from User Service) that the
 * receipt needs. @JsonIgnoreProperties(ignoreUnknown) lets this stay small
 * even though User Service's actual response has more fields (phone, role,
 * createdAt, etc.) — Payment Service simply doesn't care about those.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

    private Long id;
    private String name;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
