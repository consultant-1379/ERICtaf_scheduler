package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.collect.Lists;

import java.util.List;

public class ValidationResults {

    private List<ScheduleValidationResult> results = Lists.newArrayList();

    public void addResult(ScheduleValidationResult result) {
        results.add(result);
    }

    public boolean areValid() {
        for (ScheduleValidationResult result : results) {
            if (!result.isValid()) {
                return false;
            }
        }
        return true;
    }

    public List<ScheduleValidationResult> getResults() {
        return results;
    }

}
