package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.application.services.TestwareService;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/testware")
public class TestwareController {

    @Autowired
    private TestwareService testwareService;

    @RequestMapping(value = "/{testwareId}/suites", method = RequestMethod.GET)
    public List<String> getSuites(@PathVariable("testwareId") long testwareId) {
        return testwareService.getSuites(testwareId);
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public List<TestwareInfo> getLatestTestwareForDrop(
            @RequestParam("product") String productName,
            @RequestParam("drop") String dropName,
            @RequestParam(value = "scheduleId", required = false) Long scheduleId) {

        return testwareService.getLatestTestwareForDrop(productName, dropName, scheduleId);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public List<TestwareInfo> getLatestTestware(@RequestParam(value = "scheduleId", required = false) Long scheduleId) {
        return testwareService.getLatestTestware(scheduleId);
    }

    @RequestMapping(value = "/analyzeXml", method = RequestMethod.POST)
    public List<TestwareInfo> getTestwareListByScheduleXml(
            @RequestParam(value = "product", required = false) String productName,
            @RequestParam(value = "drop", required = false) String dropName,
            @RequestBody String scheduleXml) {
        return testwareService.getTestwareListByScheduleXml(productName, dropName, scheduleXml);
    }

}
