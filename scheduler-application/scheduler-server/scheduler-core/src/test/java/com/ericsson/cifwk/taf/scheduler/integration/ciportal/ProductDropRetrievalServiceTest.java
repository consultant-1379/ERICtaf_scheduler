package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.Drops;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ProductDropRetrievalServiceTest {

    ObjectMapper mapper = new ObjectMapper();

    MockRestServiceServer mockServer;
    ProductDropRetrievalService service;

    Drops drops;
    Drops emptyDrops;
    Drops wrongFormatDrops;

    @Before
    public void setUp() {
        service = new ProductDropRetrievalService();
        service.rest = new RestTemplate();
        service.ciPortalUrl = "http://test";

        setUpDrops();
    }

    private void setUpDrops() {
        drops = new Drops();
        List<String> rawDrops = Lists.newArrayList("CI:3.0.7",
                "CI:3.0.6",
                "CI:3.0.5",
                "CI:3.0.4",
                "CI:3.0.3",
                "CI:3.0.2");
        drops.setRawDrops(rawDrops);

        emptyDrops = new Drops();
        wrongFormatDrops = new Drops();
        wrongFormatDrops.setRawDrops(Lists.newArrayList("CI:1.1.1", "CI:", "CI11", ""));
    }

    private void setResponse(Drops drops) throws JsonProcessingException {
        mockServer = MockRestServiceServer.createServer(service.rest);
        mockServer.expect(requestTo("http://test/dropsInProduct/.json/?products=CI"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(drops), MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnFormattedDropList() throws Exception {
        setResponse(drops);

        List<DropInfo> actual = service.getDrops("CI");
        assertThat(actual, hasSize(6));
        assertThat(actual, contains(new DropInfo("CI", "3.0.7"),
                new DropInfo("CI", "3.0.6"),
                new DropInfo("CI", "3.0.5"),
                new DropInfo("CI", "3.0.4"),
                new DropInfo("CI", "3.0.3"),
                new DropInfo("CI", "3.0.2")));
    }

    @Test
    public void shouldReturnEmptyLists() throws Exception {
        setResponse(emptyDrops);

        MatcherAssert.assertThat(emptyDrops.getRawDrops(), nullValue());
        assertThat(service.getDrops("CI"), empty());
    }

    @Test
    public void shouldNotReturnIncorrectlyFormattedDrop() throws Exception {
        setResponse(wrongFormatDrops);

        List<DropInfo> actual = service.getDrops("CI");
        assertThat(actual, hasSize(1));
        assertThat(actual, contains(new DropInfo("CI", "1.1.1")));
    }
}
