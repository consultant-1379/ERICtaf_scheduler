package com.ericsson.cifwk.taf.scheduler.integration.ciportal;


import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.Drops;
import com.google.common.collect.Lists;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class ProductDropRetrievalService {

    @Value("${ci.portal.url}")
    String ciPortalUrl;

    @Autowired
    RestTemplate rest;

    @HystrixCommand(groupKey = "ci-portal", fallbackMethod = "getDropsFallback",
            commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000"),
            threadPoolProperties = @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "60000"))
    public List<DropInfo> getDrops(String product) {
        String requestUrl = buildDropsInProductRequestUrl(product);

        Drops drops = rest.getForObject(requestUrl, Drops.class);
        return Optional.ofNullable(drops)
                .map(this::formatDrops)
                .orElse(Lists.newArrayList());
    }

    @SuppressWarnings("unused")
    public List<DropInfo> getDropsFallback(String product) { //NOSONAR
        return Collections.emptyList();
    }

    private String buildDropsInProductRequestUrl(String product) {
        return ciPortalUrl + "/dropsInProduct/.json/?products=" + product;
    }

    private List<DropInfo> formatDrops(Drops drops) {
        List<String> rawDrops = drops.getRawDrops();
        if (rawDrops == null) {
            return Collections.emptyList();
        }

        return rawDrops
                .stream()
                .map(d -> {
                    String[] split = d.split(":");
                    return split.length > 1 ? new DropInfo(split[0], split[1]) : null;
                })
                .filter(d -> d != null)
                .collect(Collectors.toList());
    }

}
