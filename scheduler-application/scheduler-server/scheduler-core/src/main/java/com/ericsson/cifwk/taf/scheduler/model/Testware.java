package com.ericsson.cifwk.taf.scheduler.model;

import com.google.common.collect.Sets;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;


@Entity
@Table(name = "Testware")
public class Testware extends Artifact implements Serializable {

    @Column(name = "cxpNumber")
    private String cxpNumber;

    @ManyToMany(mappedBy = "testwares", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<TestwareIso> isos = Sets.newHashSet();

    public Testware() {
        // Empty constructor required
    }

    public Testware(Gav gav, String cxpNumber) {
        this.setGav(gav);
        this.cxpNumber = cxpNumber;
    }

    public Testware(Gav gav, String cxpNumber, TestwareIso iso) {
        this(gav, cxpNumber);
        this.addIso(iso);
    }

    public String getCxpNumber() {
        return cxpNumber;
    }

    public void setCxpNumber(String cxpNumber) {
        this.cxpNumber = cxpNumber;
    }

    public Set<TestwareIso> getIsos() {
        return isos;
    }

    public void setIsos(Set<TestwareIso> isos) {
        this.isos = isos;
    }

    public void addIso(TestwareIso iso) {
        isos.add(iso);
    }

}
