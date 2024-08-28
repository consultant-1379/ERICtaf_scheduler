package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.api.dto.ApplicationInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    @Value("${info.build.version}")
    String version;

    @Value("${info.build.name}")
    String name;

    @Value("${info.build.artifact}")
    String artifactId;

    @RequestMapping(method = RequestMethod.GET)
    public ApplicationInfo getLatestVersionOfSchedule() {
        return new ApplicationInfo(version, name, artifactId);
    }
}
