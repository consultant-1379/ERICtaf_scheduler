package com.ericsson.cifwk.taf.scheduler.integration.tms;

import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

/**
 * Created by eniakel on 24/02/2016.
 */
@Component
public class TmsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsClient.class);
    private static final String TEST_CAMPAIGNS_URL = "/test-campaigns";

    @Value("${tms.api.url}")
    String tmsUrl;

    @Autowired
    RestTemplate rest;

    private static final String RESULTS_PER_PAGE = "?perPage=";

    public List<TestCampaign.Item> findAllTestCampaignIds() {
        String requestUrl = tmsUrl + TEST_CAMPAIGNS_URL;
        LOGGER.info("Querying TAF Test Management System for Test Campaign list, url - {}", requestUrl);
        TestCampaign testCampaigns = rest.getForObject(requestUrl, TestCampaign.class);
        String numberOfTestCampaigns = testCampaigns.getTotalCount();
        requestUrl = String.format(requestUrl + "%s" + numberOfTestCampaigns, RESULTS_PER_PAGE);
        testCampaigns = rest.getForObject(requestUrl, TestCampaign.class);
        return testCampaigns.getItems();
    }
}
