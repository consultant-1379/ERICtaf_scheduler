package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoDescription;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Optional;

@Service
class IsoContentRetrievalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsoContentRetrievalService.class);

    private static final String LATEST_ISO_URL = "/getlatestisover/?product={0}&drop={1}";

    @Value("${ci.portal.url}")
    String ciPortalUrl;

    @Autowired
    RestTemplate rest;

    @HystrixCommand(fallbackMethod = "getArtifactsFallback", groupKey = "ci-portal",
            commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000"),
            threadPoolProperties = @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "60000"))
    public Optional<IsoContentHolder> getTestwareIso(String isoName, String isoVersion) {
        return getContent(isoName, isoVersion, true);
    }

    @SuppressWarnings("unused")
    private Optional<IsoContentHolder> getArtifactsFallback(String isoName, String isoVersion) { //NOSONAR
        return Optional.empty();
    }

    private Optional<IsoContentHolder> getContent(String isoName, String isoVersion, boolean showTestware) {
        String requestUrl = buildRequestUrl();
        IsoDescription iso = new IsoDescription(isoName, isoVersion);
        iso.setShowTestware(showTestware);

        try {
            IsoContentHolder content = rest.postForObject(requestUrl, iso, IsoContentHolder.class);
            return Optional.of(content);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOGGER.debug("No content found for ISO " + iso.toString(), e);
                return Optional.empty();
            }
            throw new RuntimeException("Exception during access to CI Portal", e);
        }
    }

    public String getLatestIsoVersion(String product, String drop) {
        return rest.getForObject(buildLatestIsoUrl(product, drop), String.class);
    }

    private String buildRequestUrl() {
        return ciPortalUrl + "/getPackagesInISO/";
    }

    private String buildLatestIsoUrl(String product, String drop) {
        return ciPortalUrl + MessageFormat.format(LATEST_ISO_URL, product, drop);
    }

}
