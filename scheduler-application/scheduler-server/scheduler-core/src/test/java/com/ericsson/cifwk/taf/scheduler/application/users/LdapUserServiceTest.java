package com.ericsson.cifwk.taf.scheduler.application.users;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */
public class LdapUserServiceTest {

    private LdapUserService service;
    private UserInfo userInfo;
    private LdapTemplate ldapTemplate;
    private Attributes attributes;

    @Before
    public void setUp() throws NamingException {
        ldapTemplate = mock(LdapTemplate.class);
        userInfo = mock(UserInfo.class);
        attributes = mock(Attributes.class, RETURNS_DEEP_STUBS);

        when(attributes.get(LdapUserService.MAIL).get()).thenReturn("alexey.nikolaenko@ericsson.com");
        when(attributes.get(LdapUserService.DISPLAY_NAME).get()).thenReturn("Alexey Nikolaenko");
        when(attributes.get(LdapUserService.CN).get()).thenReturn("enikoal");

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class)))
                .thenAnswer(invocationOnMock -> {
                    AttributesMapper<UserInfo> mapper = (AttributesMapper<UserInfo>) invocationOnMock.getArguments()[1];
                    return Lists.newArrayList(mapper.mapFromAttributes(attributes));
                })
                .thenReturn(Lists.newArrayList(userInfo));

        when(userInfo.getUserId()).thenReturn("enikoal");
        when(userInfo.getName()).thenReturn("Alexey Nikolaenko");
        when(userInfo.getEmail()).thenReturn("alexey.nikolaenko@ericsson.com");

        service = new LdapUserService();
        service.ldapTemplate = ldapTemplate;
    }

    @Test
    public void testFindUserBySignumOrEmail() throws Exception {
        Optional<UserInfo> maybeUser = service.findUserBySignumOrEmail("enikoal");
        validateUser(maybeUser);
    }

    @SuppressWarnings("Duplicates")
    private void validateUser(Optional<UserInfo> maybeUser) {
        if (maybeUser.isPresent()) {
            UserInfo enikoal = maybeUser.get();
            assertThat(enikoal.getUserId(), is("enikoal"));
            assertThat(enikoal.getEmail(), is("alexey.nikolaenko@ericsson.com"));
            assertThat(enikoal.getName(), is("Alexey Nikolaenko"));
        } else {
            fail();
        }
    }
}
