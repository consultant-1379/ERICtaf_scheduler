package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.User;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;


@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class UserRepositoryITest {

    private static final int RESULT_LIMIT = 5;
    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    public void shouldSaveNewUser() {
        User user = new User("user1", "Name1", "user1@ericsson.com");
        assertThat(user.getId(), is(nullValue()));

        User saved = userRepository.save(user);

        assertThat(saved.getId(), is(notNullValue()));
        assertThat(saved.getExternalId(), is("user1"));
        assertThat(saved.getName(), is("Name1"));
        assertThat(saved.getEmail(), is("user1@ericsson.com"));
    }

    @Test
    public void shouldReturnUserByExternalId() {
        User user = userRepository.findByExternalIdOrEmail("taf1");
        assertThat(user.getExternalId(), is("taf1"));
    }

    @Test
    public void shouldReturnUserByEmail() {
        User user = userRepository.findByExternalIdOrEmail("tafuser1@ericsson.com");
        assertThat(user.getEmail(), is("tafuser1@ericsson.com"));
    }

    @Test
    public void shouldReturnUserListByNameStartingWith() {
        List<User> users = userRepository.findByNameOrEmailStartingWith("ta", new PageRequest(0, RESULT_LIMIT));
        assertThat(users.size(), is(2));
        assertThat(users.get(0).getExternalId(), is("taf1"));
        assertThat(users.get(1).getExternalId(), is("taf2"));
    }

    @Test
    public void shouldLimitResultCount() {
        for (int i = 1; i <= RESULT_LIMIT + 1; i++) {
            userRepository.save(new User("user" + i, "User" + i, "tafuser" + i + "@ericsson.com"));
        }
        List<User> users = userRepository.findByNameOrEmailStartingWith("user", new PageRequest(0, RESULT_LIMIT));
        assertThat(users, Matchers.everyItem(hasProperty("externalId", startsWith("user"))));
        assertThat(users, hasSize(RESULT_LIMIT));
    }

}
