package com.ericsson.cifwk.taf.scheduler.integration.registry.model;


import com.ericsson.cifwk.taf.scheduler.api.common.GavInfo;

import javax.validation.constraints.NotNull;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
public class Testware extends GavInfo {

    @NotNull
    private String[] suites;

    public String[] getSuites() {
        return suites;
    }

    public void setSuites(String[] suites) {
        this.suites = suites;
    }
}



