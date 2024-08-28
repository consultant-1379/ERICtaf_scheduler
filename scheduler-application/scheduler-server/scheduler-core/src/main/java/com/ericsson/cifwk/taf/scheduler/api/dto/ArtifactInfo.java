package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.ericsson.cifwk.taf.scheduler.api.common.GavInfo;
import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;

public abstract class ArtifactInfo extends GavInfo {

    @NotNull
    private Long id;

    private String packaging;
    private String classifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("groupId", getGroupId())
                .add("artifactId", getArtifactId())
                .add("version", getVersion())
                .add("packaging", packaging)
                .add("classifier", classifier)
                .toString();
    }
}
