package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;

/**
 * Created by eniakel on 26/02/2016.
 */
public class TestCampaignIdValidationRule implements ValidationRule {

    private int testCampaignId;
    private List<TestCampaign.Item> testCampaigns;

    public TestCampaignIdValidationRule(int testCampaignId, List<TestCampaign.Item> testCampaigns) {
        this.testCampaignId = testCampaignId;
        this.testCampaigns = testCampaigns;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public Optional<ScheduleErrorInfo> validate(Optional<Node> maybeTestCampaign) {
        Node testCampaignNode = maybeTestCampaign.get();

        for (TestCampaign.Item testCampaign : testCampaigns) {
            if (testCampaign.getId() == testCampaignId) {
                return Optional.empty();
            }
        }
        return Optional.of(errorForItem(testCampaignNode,
                "TestCampaign with id: \"" + testCampaignId + "\" does not exist in TMS"));
    }
}
