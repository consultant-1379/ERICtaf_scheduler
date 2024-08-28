package com.ericsson.cifwk.taf.scheduler.infrastructure.security.ldap;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 12/11/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class UserContextDetailsMapperTest {

    @Mock
    EventBus eventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DirContextOperations ctx;

    @Mock
    Attribute memberOfAttribute;

    NamingEnumeration memberOfValues = new MockedNamingEnumeration(Lists.newArrayList(
            "CN=All workforce-Country-Ireland-DynDL,OU=Dynasty,OU=DDL,OU=Distribution,OU=P001,OU=GRP,OU=Data,DC=ericsson,DC=se",
            "CN=All workforce-LMI-DynDL,OU=Dynasty,OU=DDL,OU=Distribution,OU=P001,OU=GRP,OU=Data,DC=ericsson,DC=se",
            "CN=IEAT-VCD-CI1-Admin,OU=INACC,OU=P001,OU=GRP,OU=Data,DC=ericsson,DC=se",
            "CN=IEAT-VCD-ENM-Tribe7-Admin,OU=INACC,OU=P001,OU=GRP,OU=Data,DC=ericsson,DC=se",
            "CN=IEAT-VCD-TOR-KAOS-Admin,OU=INACC,OU=P001,OU=GRP,OU=Data,DC=ericsson,DC=se"));

    @InjectMocks
    UserContextDetailsMapper mapper;

    @Before
    public void setUp() throws NamingException {
        when(ctx.getAttributes().get("memberof")).thenReturn(memberOfAttribute);
        when(ctx.getStringAttribute("displayname")).thenReturn("Alexey Nikolaenko");
        when(ctx.getStringAttribute("mail")).thenReturn("alexey.nikolaenko@ericsson.com");
        when(memberOfAttribute.getAll()).thenReturn(memberOfValues);
    }

    @Test
    public void shouldReturnUserWithAllTeamAuthorities() {
        UserDetails userDetails = mapper.mapUserFromContext(ctx, "enikoal", Lists.newArrayList());

        assertThat(userDetails.getUsername(), is("enikoal"));

        Collection<? extends GrantedAuthority> userAuthorities = userDetails.getAuthorities();
        List<String> authorities = userAuthorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorities, hasItems("CI1", "ENM-Tribe7", "TOR-KAOS"));
    }

    @Test
    public void shouldReturnEmptyAuthorityList() throws NamingException {
        when(memberOfAttribute.getAll()).thenReturn(new MockedNamingEnumeration(Lists.newArrayList()));

        UserDetails userDetails = mapper.mapUserFromContext(ctx, "enikoal", Lists.newArrayList());

        assertThat(userDetails.getUsername(), is("enikoal"));

        Collection<? extends GrantedAuthority> userAuthorities = userDetails.getAuthorities();
        List<String> authorities = userAuthorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorities.isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void expectUnsupportedOperationExceptionWhenTryToMapUserBackToLdap() {
        mapper.mapUserToContext(null, null);
    }

    private class MockedNamingEnumeration implements NamingEnumeration {

        private final Iterator<String> it;

        public MockedNamingEnumeration(List<String> values) {
            it = values.iterator();
        }

        @Override
        public Object next() throws NamingException {
            return it.next();
        }

        @Override
        public boolean hasMore() throws NamingException {
            return it.hasNext();
        }

        @Override
        public void close() throws NamingException {

        }

        @Override
        public boolean hasMoreElements() {
            return it.hasNext();
        }

        @Override
        public Object nextElement() {
            return it.next();
        }
    }
}
