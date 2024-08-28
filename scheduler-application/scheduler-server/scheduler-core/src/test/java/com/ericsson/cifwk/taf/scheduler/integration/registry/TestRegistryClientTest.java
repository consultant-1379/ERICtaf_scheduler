package com.ericsson.cifwk.taf.scheduler.integration.registry;

import com.ericsson.cifwk.taf.scheduler.api.dto.ArtifactInfo;
import com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TestRegistryClientTest {

    @InjectMocks
    TestRegistryClient testRegistryClient;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ArtifactInfo artifactInfo;

    @Mock
    Testware testware;

    @Before
    public void setUp() {
        testRegistryClient.registryUrl = "http://registry.com/api";

        when(artifactInfo.getArtifactId()).thenReturn("artId");
        when(artifactInfo.getGroupId()).thenReturn("group");
        when(artifactInfo.getVersion()).thenReturn("0.0.1");
    }

    @Test
    public void shouldReturnEmptyListWhenNoArtifactsPassed() {
        List<Testware> testwareList = testRegistryClient.findTestwareDetails(Lists.newArrayList());
        assertThat(testwareList, empty());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnEmptyListWhenNullPassed() {
        testRegistryClient.findTestwareDetails(null);
    }

    @Test
    public void shouldReturnSingleTestware() {
        Testware[] tw = new Testware[1];
        tw[0] = testware;

        String requestUrl = "http://registry.com/api/testware/?search=group:artId:0.0.1";
        when(restTemplate.getForObject(eq(requestUrl), anyObject())).thenReturn(tw);

        List<Testware> testwareList = testRegistryClient.findTestwareDetails(Lists.newArrayList(artifactInfo));
        assertThat(testwareList, hasSize(1));
        verify(restTemplate, times(1)).getForObject(eq(requestUrl), anyObject());
    }

    @Test
    public void shouldReturnMultipleTestwareList() {
        Testware[] tw = new Testware[2];
        tw[0] = testware;
        tw[1] = testware;

        String requestUrl = "http://registry.com/api/testware/?search=group:artId:0.0.1,group:artId:0.0.1";
        when(restTemplate.getForObject(eq(requestUrl), anyObject())).thenReturn(tw);

        List<Testware> testwareList = testRegistryClient.findTestwareDetails(
                Lists.newArrayList(artifactInfo, artifactInfo)
        );
        assertThat(testwareList, hasSize(2));
        verify(restTemplate, times(1)).getForObject(eq(requestUrl), anyObject());
    }

    @Test
    public void shouldReturnMultipleTestwareListInBulkRequests() {
        Testware[] testwareListBulk1 = new Testware[]{testware, testware, testware, testware, testware};
        String requestUrlForBulk1 = "http://registry.com/api/testware/?search=group:artId:0.0.1,group:artId:0.0.1,group:artId:0.0.1,group:artId:0.0.1,group:artId:0.0.1";
        when(restTemplate.getForObject(eq(requestUrlForBulk1), anyObject())).thenReturn(testwareListBulk1);

        Testware[] testwareListBulk2 = new Testware[]{testware, testware};
        String requestUrlForBulk2 = "http://registry.com/api/testware/?search=group:artId:0.0.1,group:artId:0.0.1";
        when(restTemplate.getForObject(eq(requestUrlForBulk2), anyObject())).thenReturn(testwareListBulk2);

        List<Testware> testwareList = testRegistryClient.findTestwareDetails(
                Lists.newArrayList(
                        artifactInfo, artifactInfo, artifactInfo, artifactInfo, artifactInfo,
                        artifactInfo, artifactInfo
                )
        );
        assertThat(testwareList, hasSize(7));
        verify(restTemplate, times(2)).getForObject(anyString(), anyObject());
    }

    @Test
    public void shouldReturnOptionalTestwareByArtifact() {
        Testware[] tw = new Testware[1];
        tw[0] = testware;

        String requestUrl = "http://registry.com/api/testware/?search=group:artId:0.0.1";
        when(restTemplate.getForObject(eq(requestUrl), anyObject())).thenReturn(tw);

        Optional<Testware> testwareDetailsByArtifact = testRegistryClient.findTestwareDetailsByArtifact(artifactInfo);
        assertThat(testwareDetailsByArtifact.isPresent(), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnEmptyObjectWhenNullPassed() {
        testRegistryClient.findTestwareDetailsByArtifact(null);
    }
}
