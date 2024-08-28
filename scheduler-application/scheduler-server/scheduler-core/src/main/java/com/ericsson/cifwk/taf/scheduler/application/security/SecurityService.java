package com.ericsson.cifwk.taf.scheduler.application.security;

import com.ericsson.cifwk.taf.scheduler.api.dto.AuthenticationStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    private static final Authentication ANONYMOUS = new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    public static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationStatus getCurrentUser() {
        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .orElse(ANONYMOUS);

        List<String> roles = getRoles(auth);

        return new AuthenticationStatus(auth.getName(), isAuthenticated(auth), roles);
    }

    private List<String> getRoles(Authentication auth) {
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace(ROLE_PREFIX, ""))
                .collect(Collectors.toList());
    }

    private static boolean isAuthenticated(Authentication auth) {
        boolean anonymous = auth instanceof AnonymousAuthenticationToken;
        return !anonymous && auth.isAuthenticated();
    }

    public AuthenticationStatus login(UserCredentials credentials) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword());

        try {
            authentication = authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException ex) { // NOSONAR
            LOGGER.error("Bad credentials for user - " + credentials.getUsername());
            return new AuthenticationStatus(credentials.getUsername(), false, Collections.emptyList());
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        return new AuthenticationStatus(credentials.getUsername(), true, getRoles(authentication));
    }

}
