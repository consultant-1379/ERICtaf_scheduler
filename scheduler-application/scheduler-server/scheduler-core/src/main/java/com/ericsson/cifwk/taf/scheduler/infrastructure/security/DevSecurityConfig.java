package com.ericsson.cifwk.taf.scheduler.infrastructure.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@Profile({"dev", "test", "itest"})
public class DevSecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("taf").password("taf").roles("USER")
                .authorities("ROLE_CI-TAF", "ROLE_TOR-Doozers");

        auth
                .inMemoryAuthentication()
                .withUser("taf2").password("taf2").roles("USER")
                .authorities("ROLE_CI-TAF", "ROLE_ENM-Tribe7", "ROLE_TOR-KAOS");
    }
}
