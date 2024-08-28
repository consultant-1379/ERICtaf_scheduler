package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by eniakel on 08/11/2015.
 */
public class UserValidationResult {
    private Set<String> invalidUsers = Sets.newHashSet();

    public void addInvalidUser(String user) {
        invalidUsers.add(user);
    }

    public boolean isValid() {
        return invalidUsers.isEmpty();
    }

    public Set<String> getInvalidUsers() {
        return invalidUsers;
    }
}
