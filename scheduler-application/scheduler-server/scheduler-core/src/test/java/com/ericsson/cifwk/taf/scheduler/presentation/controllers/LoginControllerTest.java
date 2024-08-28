package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.application.security.SecurityService;
import com.ericsson.cifwk.taf.scheduler.api.dto.AuthenticationStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 22/07/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    @InjectMocks
    LoginController loginController;
    @Mock
    SecurityService securityService;
    @Mock
    AuthenticationStatus authStatus;
    @Mock
    UserCredentials credentials;

    @Before
    public void setUp() {
        when(securityService.getCurrentUser()).thenReturn(authStatus);
        when(securityService.login(credentials)).thenReturn(authStatus);
    }

    @Test
    public void shouldReturnResponseWithUserInfo() {
        ResponseEntity<AuthenticationStatus> status = loginController.status();
        assertThat(status.getBody(), is(authStatus));
        assertThat(status.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnOkIfLoggedIn() {
        when(authStatus.isAuthenticated()).thenReturn(true);

        ResponseEntity<AuthenticationStatus> response = loginController.login(credentials);
        assertThat(response.getBody(), is(authStatus));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnUnauthorizedIfLoginFailed() {
        when(authStatus.isAuthenticated()).thenReturn(false);

        ResponseEntity<AuthenticationStatus> response = loginController.login(credentials);
        assertThat(response.getBody(), is(authStatus));
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }
}
