package com.ericsson.cifwk.taf.scheduler.api.dto;

import java.util.List;

public class AuthenticationStatus {

    private boolean authenticated;
    private String userId;
    private List<String> roles;

    public AuthenticationStatus() {
        // Empty Constructor required
    }

    public AuthenticationStatus(String userId, boolean authenticated, List<String> roles) {
        this.userId = userId;
        this.authenticated = authenticated;
        this.roles = roles;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getRoles() {
        return roles;
    }
}
