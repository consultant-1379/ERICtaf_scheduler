package com.ericsson.cifwk.taf.scheduler.integration.registry;

import com.ericsson.cifwk.taf.scheduler.api.dto.ArtifactInfo;
import com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware;
import com.google.common.collect.Lists;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
@Component
public class TestRegistryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRegistryClient.class);

    private static final int BULK_LIMIT = 5;

    @Value("${test-registry.api.url}")
    String registryUrl;

    @Autowired
    RestTemplate rest;

    @HystrixCommand(fallbackMethod = "findTestwareDetailsFallback", groupKey = "taf-registry")
    public List<Testware> findTestwareDetails(List<? extends ArtifactInfo> artifacts) {
        List<String> testwareBulks = buildTestwareBulks(artifacts);
        if (testwareBulks.isEmpty()) {
            return Lists.newArrayList();
        }

        List<Testware> testwareList = Lists.newArrayList();
        for (String testwareBulk : testwareBulks) {
            String requestUrl = registryUrl + String.format("/testware/?search=%s", testwareBulk);
            LOGGER.info("Querying TAF Registry for testware list, url - {}", requestUrl);
            Testware[] trTestwareList = rest.getForObject(requestUrl, Testware[].class);
            testwareList.addAll(Lists.newArrayList(trTestwareList));
        }
        return testwareList;
    }

    @HystrixCommand(fallbackMethod = "findTestwareDetailsByArtifactFallback", groupKey = "taf-registry")
    public <T extends ArtifactInfo> Optional<Testware> findTestwareDetailsByArtifact(T artifact) {
        String testwareDetails = String.format("%s:%s:%s",
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion());

        String requestUrl = registryUrl + String.format("/testware/?search=%s", testwareDetails);
        LOGGER.info("Querying TAF Registry for testware list, url - {}", requestUrl);
        Testware[] testwareList = rest.getForObject(requestUrl, Testware[].class);
        if (testwareList.length > 0) {
            return Optional.of(testwareList[0]);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    public <T extends ArtifactInfo> Optional<Testware> findTestwareDetailsByArtifactFallback(T artifact) { //NOSONAR
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    private List<Testware> findTestwareDetailsFallback(List<? extends ArtifactInfo> artifacts) { //NOSONAR
        return Lists.newArrayList();
    }

    private static List<String> buildTestwareBulks(List<? extends ArtifactInfo> artifacts) {
        List<String> testwareBulks = Lists.newArrayList();

        StringJoiner joiner = new StringJoiner(",");
        int count = 0;
        for (ArtifactInfo artifact : artifacts) {
            count++;
            joiner.add(String.format("%s:%s:%s",
                    artifact.getGroupId(),
                    artifact.getArtifactId(),
                    artifact.getVersion()));

            if (count >= BULK_LIMIT) {
                count = 0;
                testwareBulks.add(joiner.toString());
                joiner = new StringJoiner(",");
            }
        }
        if (count > 0 && count < BULK_LIMIT) {
            testwareBulks.add(joiner.toString());
        }
        return testwareBulks;
    }
}
