package com.ericsson.cifwk.taf.scheduler.application.security;

import com.google.common.collect.Lists;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by evlailj on 17/06/2015.
 */
public class SecurityMock {

    public static void mockPrincipal(String user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        doReturn(authorities()).when(authentication).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static List<? extends GrantedAuthority> authorities() {
        return Lists.newArrayList(
                new SimpleGrantedAuthority("CI-TAF"),
                new SimpleGrantedAuthority("TOR-KAOS")
        );
    }

    public static void mockLdapPrincipal(String user) {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user);
        doReturn(authorities()).when(authentication).getAuthorities();

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
