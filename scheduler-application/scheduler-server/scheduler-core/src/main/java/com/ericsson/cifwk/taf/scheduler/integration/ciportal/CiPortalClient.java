package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.LatestTestwareHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class CiPortalClient {

    @Autowired
    IsoContentRetrievalService isoContentService;

    @Autowired
    ProductDropRetrievalService dropService;

    @Autowired
    LatestTestwareRetrievalService testwareRetrievalService;

    public List<DropInfo> getDrops(String productName) {
        return dropService.getDrops(productName);
    }

    public Optional<IsoContentHolder> getTestwareIso(String isoName, String isoVersion) {
        return isoContentService.getTestwareIso(isoName, isoVersion);
    }

    public String getLatestIsoVersion(String product, String drop) {
        return isoContentService.getLatestIsoVersion(product, drop);
    }

    public Optional<LatestTestwareHolder> getLatestTestware() {
        return testwareRetrievalService.getLatestTestware();
    }
}
