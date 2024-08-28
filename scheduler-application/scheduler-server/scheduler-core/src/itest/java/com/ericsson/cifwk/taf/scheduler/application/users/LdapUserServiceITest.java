package com.ericsson.cifwk.taf.scheduler.application.users;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class LdapUserServiceITest {

    @Autowired
    LdapUserService ldapUserService;

    @Test
    public void testFindUserBySignumOrEmail() {
        Optional<UserInfo> maybeUser = ldapUserService.findUserBySignumOrEmail("egergle");
        validateUser(maybeUser);

        maybeUser = ldapUserService.findUserBySignumOrEmail("gerald.glennon@ericsson.com");
        validateUser(maybeUser);
    }

    @SuppressWarnings("Duplicates")
    private void validateUser(Optional<UserInfo> maybeUser) {
        if (maybeUser.isPresent()) {
            UserInfo enikoal = maybeUser.get();
            assertThat(enikoal.getUserId(), is("egergle"));
            assertThat(enikoal.getEmail(), is("gerald.glennon@ericsson.com"));
            assertThat(enikoal.getName(), is("Gerald Glennon"));
        } else {
            fail();
        }
    }
}
