package com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */
@Service
public class UserContextDetailsMapper implements UserDetailsContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextDetailsMapper.class);
    private static final Pattern TEAM_VALUE_PATTERN = Pattern.compile("IEAT-VCD-(.+)-(Admin|User)");

    @Autowired
    EventBus eventBus;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String name = ctx.getStringAttribute("displayname");
        String email = ctx.getStringAttribute("mail");
        eventBus.post(new UserInfo(username, name, email));

        Set<String> teams = getUserTeams(ctx);

        Set<GrantedAuthority> userTeamAuthorities = teams
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        userTeamAuthorities.addAll(authorities);

        return new User(username, "", userTeamAuthorities);
    }

    private Set<String> getUserTeams(DirContextOperations ctx) {
        Set<String> teams = Sets.newHashSet();

        Attribute memberOfAttibutes = ctx.getAttributes().get("memberof");
        if (memberOfAttibutes != null) {
            try {
                NamingEnumeration<?> memberOfValues = memberOfAttibutes.getAll();
                while (memberOfValues.hasMore()) {
                    String value = memberOfValues.nextElement().toString();

                    Optional<String> team = findTeamValue(value);
                    team.ifPresent(teams::add);
                }
            } catch (NamingException e) {
                LOGGER.error("Exception parsing LDAP 'memberOf' attribute", e);
            }
        }
        return teams;
    }

    private Optional<String> findTeamValue(String value) {
        Matcher matcher = TEAM_VALUE_PATTERN.matcher(value);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("Saving user to ldap not supported");
    }
}
