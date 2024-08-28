package com.ericsson.cifwk.taf.scheduler.api.dto;

import javax.validation.constraints.NotNull;

public class UserCredentials {

    @NotNull
    private String username;
    @NotNull
    private String password;

    public UserCredentials() {
        // Empty Constructor required
    }

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
