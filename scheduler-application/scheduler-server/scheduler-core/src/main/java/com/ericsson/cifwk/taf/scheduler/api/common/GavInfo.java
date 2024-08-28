package com.ericsson.cifwk.taf.scheduler.api.common;

import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 15/07/2015
 */
public class GavInfo {

    @NotNull
    private String groupId;
    @NotNull
    private String artifactId;
    @NotNull
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GavInfo gavInfo = (GavInfo) o;
        return Objects.equal(groupId, gavInfo.groupId) &&
                Objects.equal(artifactId, gavInfo.artifactId) &&
                Objects.equal(version, gavInfo.version);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId, artifactId, version);
    }
}
