package com.ericsson.cifwk.taf.scheduler.integration.registry;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware;
import com.google.common.collect.Lists;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class TestRegistryClientITest {

    @Autowired
    TestRegistryClient client;
    HystrixRequestContext context;
    RestTemplate failingRestTemplate;
    TestwareInfo mockedTestwareInfo;

    @Before
    public void setUp() {
        context = HystrixRequestContext.initializeContext();

        failingRestTemplate = Mockito.mock(RestTemplate.class);
        when(failingRestTemplate.getForObject(any(), any())).thenThrow(new RuntimeException());

        mockedTestwareInfo = Mockito.mock(TestwareInfo.class);
        when(mockedTestwareInfo.getGroupId()).thenReturn("com.ericsson.bcg.testware");
        when(mockedTestwareInfo.getArtifactId()).thenReturn("ERICTAFbcg_CXP9030907");
        when(mockedTestwareInfo.getVersion()).thenReturn("1.9.201");
    }

    /**
     * Test uses TAF Registry production environment
     */
    @Test
    public void shouldReturnResponse() {
        List<Testware> testwareDetails = client.findTestwareDetails(Lists.newArrayList(mockedTestwareInfo));

        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();

        assertThat("Response from fallback is returned, TAF Registry prod env may have issues",
                command.isResponseFromFallback(), is(false));
        assertThat(testwareDetails, hasSize(1));
    }

    @After
    public void tearDown() {
        context.shutdown();
    }
}
