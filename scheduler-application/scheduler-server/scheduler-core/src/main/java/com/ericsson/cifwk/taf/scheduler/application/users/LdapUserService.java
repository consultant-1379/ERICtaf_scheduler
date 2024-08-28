package com.ericsson.cifwk.taf.scheduler.application.users;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */
@Service
public class LdapUserService {

    public static final String MAIL = "mail";
    public static final String ACCOUNT_NAME = "sAMAccountName";
    public static final String DISPLAY_NAME = "displayname";
    public static final String CN = "cn";

    @Autowired
    LdapTemplate ldapTemplate;

    public Optional<UserInfo> findUserBySignumOrEmail(String signumOrEmail) {
        LdapQuery query = query().where(ACCOUNT_NAME).is(signumOrEmail).or(MAIL).is(signumOrEmail);

        return findSingle(query);
    }

    private Optional<UserInfo> findSingle(LdapQuery query) {
        List<UserInfo> result = ldapTemplate.search(query,
                userInfoAttributesMapper());

        return result.size() == 1 ? Optional.of(result.get(0)) : Optional.empty();
    }

    private static AttributesMapper<UserInfo> userInfoAttributesMapper() {
        return attrs -> {
            String email = (String) attrs.get(MAIL).get();
            String name = (String) attrs.get(DISPLAY_NAME).get();
            String signum = (String) attrs.get(CN).get();
            return new UserInfo(signum, name, email);
        };
    }
}
