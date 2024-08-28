package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.application.security.SecurityService;
import com.ericsson.cifwk.taf.scheduler.api.dto.AuthenticationStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    SecurityService securityService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<AuthenticationStatus> status() {
        AuthenticationStatus user = securityService.getCurrentUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AuthenticationStatus> login(@Valid @RequestBody UserCredentials credentials) {
        AuthenticationStatus status = securityService.login(credentials);
        return new ResponseEntity<>(status, status.isAuthenticated() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }
}
