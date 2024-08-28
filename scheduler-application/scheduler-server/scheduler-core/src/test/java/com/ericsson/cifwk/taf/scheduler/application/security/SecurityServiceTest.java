package com.ericsson.cifwk.taf.scheduler.application.security;

import com.ericsson.cifwk.taf.scheduler.api.dto.AuthenticationStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserCredentials;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {
    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    Authentication authentication;

    @InjectMocks
    SecurityService securityService = new SecurityService();

    Collection<? extends GrantedAuthority> authorityList = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_CI-TAF"),
            new SimpleGrantedAuthority("ROLE_TOR-KAOS")
    );

    UserCredentials userCredentials;

    @Before
    public void setUp() {
        userCredentials = new UserCredentials("user", "pass");

        when(authentication.getName()).thenReturn(userCredentials.getUsername());
        when(authentication.getCredentials()).thenReturn(userCredentials.getPassword());

        doReturn(authorityList).when(authentication).getAuthorities();

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    }

    @Test
    public void shouldReturnAnonymousUser() {
        AuthenticationStatus currentUser = securityService.getCurrentUser();
        assertThat(currentUser.isAuthenticated(), is(false));
        assertThat(currentUser.getUserId(), is("anonymousUser"));
        assertThat(currentUser.getRoles().size(), is(1));
        assertThat(currentUser.getRoles(), hasItems("ANONYMOUS"));
    }

    @Test
    public void shouldReturnUserFromContext() {
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthenticationStatus currentUser = securityService.getCurrentUser();
        assertThat(currentUser.isAuthenticated(), is(true));
        assertThat(currentUser.getUserId(), is("user"));
        assertThat(currentUser.getRoles().size(), is(2));
        assertThat(currentUser.getRoles(), hasItems("CI-TAF", "TOR-KAOS"));
    }

    @Test
    public void testLogin() {
        AuthenticationStatus login = securityService.login(userCredentials);

        assertThat(login.getUserId(), is("user"));
        assertThat(login.isAuthenticated(), is(true));
        assertThat(login.getRoles().size(), is(2));
        assertThat(login.getRoles(), hasItems("CI-TAF", "TOR-KAOS"));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
