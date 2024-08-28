package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.ericsson.cifwk.taf.scheduler.integration.ciportal.ResponseCreator.withNotFound;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class IsoContentRetrievalServiceTest {

    ObjectMapper mapper = new ObjectMapper();

    MockRestServiceServer mockServer;

    IsoContentRetrievalService service;

    @Before
    public void setUp() throws JsonProcessingException {
        service = new IsoContentRetrievalService();
        service.rest = new RestTemplate();
        service.ciPortalUrl = "localhost";
        mockServer = MockRestServiceServer.createServer(service.rest);
    }

    @Test
    public void testGetTestware() throws JsonProcessingException {
        String responseJson = mapper.writeValueAsString(getExpectedIsoContentHolder());
        String expectedRequestJson = mapper.writeValueAsString(getExpectedIsoDescription("ISO_NAME", "ISO_VERSION", true));
        mockServer.expect(requestTo("localhost/getPackagesInISO/"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(expectedRequestJson))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Optional<IsoContentHolder> content = service.getTestwareIso("ISO_NAME", "ISO_VERSION");
        List<CiArtifact> testware = content.get().getArtifactList();
        assertThat(testware.size(), is(1));
    }

    private IsoContentHolder getExpectedIsoContentHolder() {
        IsoContentHolder isoContentHolder = new IsoContentHolder();
        isoContentHolder.setProductIsoVersionVersion("1.0");
        isoContentHolder.setProductIsoName("ISO_NAME");

        CiArtifact artifact = new CiArtifact();
        artifact.setName("artifactName");
        artifact.setVersion("version");
        artifact.setGroup("group");
        isoContentHolder.setArtifactList(Lists.newArrayList(artifact));

        return isoContentHolder;
    }

    private IsoDescription getExpectedIsoDescription(String ISO_NAME, String ISO_VERSION, boolean showTestware) {
        IsoDescription iso = new IsoDescription(ISO_NAME, ISO_VERSION);
        iso.setShowTestware(showTestware);
        return iso;
    }
}
