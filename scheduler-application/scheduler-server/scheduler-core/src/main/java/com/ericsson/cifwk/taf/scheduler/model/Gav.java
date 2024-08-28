package com.ericsson.cifwk.taf.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Gav implements Serializable {
    @Column(name = "groupId", nullable = false)
    private String groupId;
    @Column(name = "artifactId", nullable = false)
    private String artifactId;
    @Column(name = "version", nullable = false)
    private String version;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
