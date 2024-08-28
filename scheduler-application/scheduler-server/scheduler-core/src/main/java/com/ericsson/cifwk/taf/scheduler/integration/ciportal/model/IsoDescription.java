package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * This object is used as Request body for CI Portal REST Service
 * which returns Testware or Packages for CiIso
 */
public class IsoDescription {

    @JsonProperty("isoName")
    private String isoName;
    @JsonProperty("isoVersion")
    private String isoVersion;

    @JsonProperty("pretty")
    private boolean pretty;
    /**
     * If true REST service returns testware artifact list
     * Otherwise Package artifact list is returned
     */
    @JsonProperty("showTestware")
    private boolean showTestware;

    public IsoDescription() {
        // Empty constructor required
    }

    public IsoDescription(String isoName, String isoVersion) {
        this.isoName = isoName;
        this.isoVersion = isoVersion;
    }

    public String getIsoName() {
        return isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    public boolean isPretty() {
        return pretty;
    }

    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }

    public boolean isShowTestware() {
        return showTestware;
    }

    public void setShowTestware(boolean showTestware) {
        this.showTestware = showTestware;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isoName", isoName)
                .add("isoVersion", isoVersion)
                .add("showTestware", showTestware)
                .add("pretty", pretty)
                .toString();
    }
}
