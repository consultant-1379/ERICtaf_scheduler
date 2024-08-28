package com.ericsson.cifwk.taf.scheduler.infrastructure.security;

import com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap.LdapConfiguration;
import com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap.UserContextDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class ProdSecurityConfig extends BaseSecurityConfig {

    @Autowired
    private LdapConfiguration ldapConfiguration;

    @Autowired
    private UserContextDetailsMapper userContextDetailsMapper;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userDetailsContextMapper(userContextDetailsMapper)
                .userDnPatterns(ldapConfiguration.getDnPattern())
                .groupSearchBase(ldapConfiguration.getSearchBase())
                .contextSource()
                .managerDn(ldapConfiguration.getManagerDn())
                .managerPassword(ldapConfiguration.getManagerPassword())
                .url(ldapConfiguration.getUrl());
    }
}
