package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserValidationResult;
import com.ericsson.cifwk.taf.scheduler.application.users.UserService;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         30/10/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;
    @Mock
    UserMapper userMapper;

    Set<UserInfo> users;

    @Before
    public void setUp() throws Exception {
        users = Sets.newHashSet(new UserInfo("taf", "Name", "taftest@ericsson.com"));
    }

    @Test
    public void getUsers_shouldReturnEmptyListIfNameIsBlank() throws Exception {
        Set<UserInfo> users = userController.getUsers("", 10);
        assertThat(users, Matchers.empty());
    }

    @Test
    public void getUsers_shouldReturnUserList() throws Exception {
        when(userService.findByNameOrEmailStartingWith("taf", 10)).thenReturn(Collections.<User>emptyList());
        when(userMapper.map(anyList())).thenReturn(users);

        Set<UserInfo> users = userController.getUsers("taf", 10);
        assertThat(users, Matchers.hasSize(users.size()));
    }

    @Test
    public void getUsers_shouldValidateReviewers() throws Exception {
        when(userService.findInvalidReviewers(new ArrayList<>())).thenReturn(new UserValidationResult());
        ResponseEntity<UserValidationResult> response = userController.validateReviewers(new ArrayList<>());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().isValid(), is(true));
    }
}
