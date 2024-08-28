package com.ericsson.cifwk.taf.scheduler.integration.tms;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by eniakel on 25/02/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class TmsClientITest {

    @Autowired
    TmsClient client;
    Set<String> testCampaignIds;

    @Before
    public void setUp() {
        testCampaignIds = Sets.newHashSet();
        testCampaignIds.add("Invalid");
    }

    /**
     * Test uses TAF Test Management production environment
     */
    @Test
    public void shouldReturnAllValidTestCampaigns() {
        List<TestCampaign.Item> validTestCampaigns = client.findAllTestCampaignIds();
        assertThat(validTestCampaigns.isEmpty(), is(false));
    }

}
