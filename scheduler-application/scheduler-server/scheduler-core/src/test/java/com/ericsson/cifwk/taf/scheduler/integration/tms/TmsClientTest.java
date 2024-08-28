package com.ericsson.cifwk.taf.scheduler.integration.tms;

import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Created by eniakel on 25/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TmsClientTest {
    private static final int TEST_CAMPAIGN_A_ID = 1;
    private static final int TEST_CAMPAIGN_B_ID = 2;
    private static final String REQUEST_URL_BASE = "http://tms.com/api/test-campaigns";

    @InjectMocks
    TmsClient tmsClient;

    @Mock
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        tmsClient.tmsUrl = "http://tms.com/api";

        TestCampaign testCampaigns = generateTestCampaigns();
        String requestUrlWithLimit = REQUEST_URL_BASE + "?perPage=2";
        when(restTemplate.getForObject(eq(REQUEST_URL_BASE), anyObject())).thenReturn(testCampaigns);
        when(restTemplate.getForObject(eq(requestUrlWithLimit), anyObject())).thenReturn(testCampaigns);
    }

    @Test
    public void shouldReturnAllTestCampaignIdsThatExist() {
        List<TestCampaign.Item> validTestCampaigns = tmsClient.findAllTestCampaignIds();
        assertThat(validTestCampaigns, hasSize(2));
    }

    private TestCampaign generateTestCampaigns() {
        TestCampaign.Item firstItem = new TestCampaign.Item();
        firstItem.setId(TEST_CAMPAIGN_A_ID);

        TestCampaign.Item secondItem = new TestCampaign.Item();
        secondItem.setId(TEST_CAMPAIGN_B_ID);

        List<TestCampaign.Item> items = Lists.newArrayList();
        items.add(firstItem);
        items.add(secondItem);

        TestCampaign testCampaigns = new TestCampaign();
        testCampaigns.setTotalCount("2");
        testCampaigns.setItems(items);
        return testCampaigns;
    }
}
