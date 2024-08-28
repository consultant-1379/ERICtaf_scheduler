package com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */
@Configuration
public class LdapContextSourceConfiguration {

    @Autowired
    LdapConfiguration ldapConfiguration;


    @Bean(name = "ldapTemplate")
    public LdapTemplate ldapTemplateBean() {
        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl(ldapConfiguration.getUrl());
        ctxSrc.setBase(ldapConfiguration.getSearchBase());
        ctxSrc.setUserDn(ldapConfiguration.getManagerDn());
        ctxSrc.setPassword(ldapConfiguration.getManagerPassword());
        ctxSrc.afterPropertiesSet();

        return new LdapTemplate(ctxSrc);
    }
}
