package com.ericsson.cifwk.taf.scheduler.api.dto;

import org.hibernate.validator.constraints.Email;

public class UserInfo {

    private String userId;
    private String name;
    @Email
    private String email;

    public UserInfo() {
        // Empty Constructor required
    }

    public UserInfo(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public UserInfo(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserInfo)) {
            return false;
        }
        UserInfo userInfo = (UserInfo) o;
        return getUserId() != null && getUserId().equals(userInfo.getUserId()) ||
                (getEmail() != null && getEmail().equals(userInfo.getEmail()));
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
