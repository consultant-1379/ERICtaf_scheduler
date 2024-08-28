package com.ericsson.cifwk.taf.scheduler.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class Artifact extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "packaging")
    private String packaging;
    @Column(name = "classifier")
    private String classifier;
    @Embedded
    private Gav gav;

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

    public void setGav(Gav gav) {
        this.gav = gav;
    }

    public Gav getGav() {
        return gav;
    }
}
