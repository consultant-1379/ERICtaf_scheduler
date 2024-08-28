package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.LatestTestwareHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class LatestTestwareRetrievalService {

    private static final String LATEST_TESTWARE_URL = "/getLatestTestware/";

    @Value("${ci.portal.url}")
    String ciPortalUrl;

    @Autowired
    RestTemplate rest;

    @HystrixCommand(fallbackMethod = "getLatestTestwareFallback", groupKey = "ci-portal",
            commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000"),
            threadPoolProperties = @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "60000"))
    public Optional<LatestTestwareHolder> getLatestTestware() {
        LatestTestwareHolder latestTestware = rest.getForObject(ciPortalUrl + LATEST_TESTWARE_URL, LatestTestwareHolder.class);
        return Optional.of(latestTestware);
    }

    @SuppressWarnings("unused")
    private Optional<LatestTestwareHolder> getLatestTestwareFallback() { // NOSONAR
        return Optional.empty();
    }
}
