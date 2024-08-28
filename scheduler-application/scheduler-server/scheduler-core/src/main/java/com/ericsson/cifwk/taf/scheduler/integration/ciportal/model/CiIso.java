package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;

public class CiIso {

    @NotNull
    @JsonProperty("mediaArtifactName")
    private String isoName;
    @NotNull
    @JsonProperty("version")
    private String isoVersion;

    public CiIso() {
        // Empty constructor required
    }

    public String getIsoName() {
        return "none".equalsIgnoreCase(isoName) ? null : isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public String getIsoVersion() {
        return "none".equalsIgnoreCase(isoVersion) ? null : isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    public boolean exists() {
        return getIsoName() != null && getIsoVersion() != null;
    }

    @Override
    public String toString() {
        return "CiIso{" +
                "isoName='" + isoName + '\'' +
                ", isoVersion='" + isoVersion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CiIso iso = (CiIso) o;
        return Objects.equal(isoName, iso.isoName) &&
                Objects.equal(isoVersion, iso.isoVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isoName, isoVersion);
    }
}
