package com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LdapConfiguration {

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.managerDn}")
    private String managerDn;

    @Value("${ldap.managerPassword}")
    private String managerPassword;

    @Value("${ldap.search.base}")
    private String searchBase;

    @Value("${ldap.dnPattern}")
    private String dnPattern;

    public String getDnPattern() {
        return dnPattern;
    }

    public String getUrl() {
        return url;
    }

    public String getManagerDn() {
        return managerDn;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public String getSearchBase() {
        return searchBase;
    }
}
