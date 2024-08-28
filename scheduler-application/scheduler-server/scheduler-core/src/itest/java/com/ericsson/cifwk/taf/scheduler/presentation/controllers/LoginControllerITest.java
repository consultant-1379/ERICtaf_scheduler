package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.api.dto.AuthenticationStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class LoginControllerITest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    Filter springSecurityFilterChain;

    MockMvc mockMvc;
    Gson gson;
    SessionHolder sessionHolder;
    ObjectMapper om;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
        gson = new Gson();
        om = new ObjectMapper();
    }

    @Test
    public void testLogin() throws Exception {
        performLogin();

        AuthenticationStatus loginAuthStatus = getAuthenticationStatus();
        assertThat(loginAuthStatus.isAuthenticated(), is(true));
        assertThat(loginAuthStatus.getRoles(), hasItems("CI-TAF", "TOR-Doozers"));
    }

    @Test
    public void testLogout() throws Exception {
        performLogin();

        AuthenticationStatus loginAuthStatus = getAuthenticationStatus();
        assertThat(loginAuthStatus.isAuthenticated(), is(true));
        assertThat(loginAuthStatus.getRoles(), hasItems("CI-TAF", "TOR-Doozers"));

        performLogout();

        AuthenticationStatus logoutAuthStatus = getAuthenticationStatus();
        assertThat(logoutAuthStatus.isAuthenticated(), is(false));
        assertThat(logoutAuthStatus.getRoles(), hasItem("ANONYMOUS"));
        assertThat(logoutAuthStatus.getRoles().size(), is(1));
    }

    private AuthenticationStatus getAuthenticationStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/login").session(sessionHolder))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return om.readValue(mvcResult.getResponse().getContentAsString(), AuthenticationStatus.class);
    }

    private void performLogin() throws Exception {
        String credentials = gson.toJson(new UserCredentials("taf", "taf"));

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isOk()).andDo(result -> sessionHolder = new SessionHolder(result.getRequest().getSession()));
    }

    private void performLogout() throws Exception {
        mockMvc.perform(delete("/api/login").session(sessionHolder))
                .andExpect(status().isOk())
                .andReturn();
    }

    private static class SessionHolder extends MockHttpSession {
        private HttpSession session;

        public SessionHolder(HttpSession session) {
            this.session = session;
        }

        @Override
        public Object getAttribute(String name) {
            return session.getAttribute(name);
        }
    }
}
