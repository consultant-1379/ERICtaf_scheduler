package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.security.SecurityMock;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserValidationResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         30/10/2015
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class UserControllerITest {

    //Users already in database
    private static final String REVIEWER1 = "tafuser1@ericsson.com";
    private static final String REVIEWER2 = "taf2";
    //User available from LDAP lookup
    private static final String REVIEWER3 = "eniakel";
    //Invalid user
    private static final String REVIEWER4 = "invalidReviewer";

    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;
    ObjectMapper om;
    ConfiguredGson gson;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        gson = new ConfiguredGson();
        SecurityMock.mockPrincipal("evlailj");
    }

    @Test
    public void shouldReturnUsersByNameStartingWith() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/users")
                .param("name", "taf"))
                .andExpect(status().isOk())
                .andReturn();
        List<UserInfo> users = readUsers(mvcResult);

        assertThat(users.size(), is(2));

        assertThat(users.get(0).getUserId(), is("taf1"));
        assertThat(users.get(1).getUserId(), is("taf2"));

        assertThat(users.get(0).getName(), is("TafUser1"));
        assertThat(users.get(1).getName(), is("TafUser2"));
    }

    @Test
    public void shouldValidateReviewers() throws Exception {
        List<String> reviewers = Lists.newArrayList();
        reviewers.add(REVIEWER1);
        reviewers.add(REVIEWER2);
        reviewers.add(REVIEWER3);

        //validation check with all reviewers valid
        MvcResult mvcResult = mockMvc.perform(post("/api/users/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(reviewers)))
                .andReturn();

        UserValidationResult userValidationResult = om.readValue(mvcResult.getResponse().getContentAsString(),
                UserValidationResult.class);

        Set<String> invalidUsers = userValidationResult.getInvalidUsers();
        assertTrue(invalidUsers.isEmpty());
    }


    @Test
    public void shouldFailToValidateReviewers() throws Exception {
        List<String> reviewers = Lists.newArrayList();
        reviewers.add(REVIEWER1);
        reviewers.add(REVIEWER2);
        reviewers.add(REVIEWER3);
        reviewers.add(REVIEWER4);

        //validation check with invalid reviewer present
        MvcResult mvcResult = mockMvc.perform(post("/api/users/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(reviewers)))
                .andReturn();

        UserValidationResult userValidationResult = om.readValue(mvcResult.getResponse().getContentAsString(),
                UserValidationResult.class);

        Set<String> invalidUsers = userValidationResult.getInvalidUsers();
        assertThat(invalidUsers.size(), is(1));

        Iterator iterator = invalidUsers.iterator();
        assertThat(iterator.next(), is(REVIEWER4));
    }

    private List<UserInfo> readUsers(MvcResult mvcResult) throws java.io.IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserInfo>>() {
        });
    }
}
