package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IsoContentHolder {

    @NotNull
    @JsonProperty("ISOName")
    private String productIsoName;

    @NotNull
    @JsonProperty("ISOVersion")
    private String productIsoVersion;

    @NotNull
    @JsonProperty("TestwareISOName")
    private String testwareIsoName;

    @NotNull
    @JsonProperty("TestwareISOVersion")
    private String testwareIsoVersion;

    @NotNull
    @JsonProperty("PackagesInISO")
    private List<CiArtifact> artifactList;

    public String getProductIsoName() {
        return productIsoName;
    }

    public void setProductIsoName(String productIsoName) {
        this.productIsoName = productIsoName;
    }

    public String getProductIsoVersion() {
        return productIsoVersion;
    }

    public void setProductIsoVersionVersion(String productIsoVersion) {
        this.productIsoVersion = productIsoVersion;
    }

    public String getTestwareIsoName() {
        return testwareIsoName;
    }

    public void setTestwareIsoName(String testwareIsoName) {
        this.testwareIsoName = testwareIsoName;
    }

    public String getTestwareIsoVersion() {
        return testwareIsoVersion;
    }

    public void setTestwareIsoVersion(String testwareIsoVersion) {
        this.testwareIsoVersion = testwareIsoVersion;
    }

    public List<CiArtifact> getArtifactList() {
        return artifactList == null ? Lists.newArrayList() : artifactList;
    }

    public void setArtifactList(List<CiArtifact> artifactList) {
        this.artifactList = artifactList;
    }
}
