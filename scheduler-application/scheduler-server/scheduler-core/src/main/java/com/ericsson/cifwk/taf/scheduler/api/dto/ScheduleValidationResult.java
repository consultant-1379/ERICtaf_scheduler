package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */
public class ScheduleValidationResult {

    private ScheduleInfo schedule;

    private List<ScheduleErrorInfo> suiteErrors = Lists.newArrayList();

    private List<ScheduleErrorInfo> schemaErrors = Lists.newArrayList();

    private List<ScheduleErrorInfo> includeErrors = Lists.newArrayList();

    private List<ScheduleErrorInfo> testCampaignErrors = Lists.newArrayList();

    public ScheduleInfo getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleInfo schedule) {
        this.schedule = schedule;
    }

    public List<ScheduleErrorInfo> getSuiteErrors() {
        return suiteErrors;
    }

    public void setSuiteErrors(List<ScheduleErrorInfo> suiteErrors) {
        this.suiteErrors = suiteErrors;
    }

    public List<ScheduleErrorInfo> getSchemaErrors() {
        return schemaErrors;
    }

    public void setSchemaErrors(List<ScheduleErrorInfo> schemaErrors) {
        this.schemaErrors = schemaErrors;
    }

    public List<ScheduleErrorInfo> getIncludeErrors() {
        return includeErrors;
    }

    public void setIncludeErrors(List<ScheduleErrorInfo> includeErrors) {
        this.includeErrors = includeErrors;
    }

    public List<ScheduleErrorInfo> getTestCampaignErrors() {
        return testCampaignErrors;
    }

    public void setTestCampaignErrors(List<ScheduleErrorInfo> testCampaignErrors) {
        this.testCampaignErrors = testCampaignErrors;
    }

    public boolean isValid() {
        return suiteErrors.isEmpty() && schemaErrors.isEmpty() && includeErrors.isEmpty() && testCampaignErrors.isEmpty();
    }
}
