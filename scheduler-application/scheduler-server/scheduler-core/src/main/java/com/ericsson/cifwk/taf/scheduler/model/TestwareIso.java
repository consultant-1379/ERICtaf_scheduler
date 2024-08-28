package com.ericsson.cifwk.taf.scheduler.model;

import com.google.common.collect.Sets;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "TestwareIso")
public class TestwareIso extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "latestInDrop")
    private boolean latestInDrop;

    @ManyToOne
    @JoinColumn(name = "isoId", nullable = false)
    private ISO iso;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "IsoTestwares",
            joinColumns = {@JoinColumn(name = "testwareIsoId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "testwareId", referencedColumnName = "id")})
    private Set<Testware> testwares = Sets.newHashSet();

    public TestwareIso() {
        // Empty constructor required
    }

    public TestwareIso(String name, String version, ISO iso) {
        this.name = name;
        this.version = version;
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isLatestInDrop() {
        return latestInDrop;
    }

    public void setLatestInDrop(boolean latestInDrop) {
        this.latestInDrop = latestInDrop;
    }

    public ISO getIso() {
        return iso;
    }

    public void setIso(ISO iso) {
        this.iso = iso;
    }

    public Set<Testware> getTestwares() {
        return testwares;
    }

    public void addTestware(Testware t) {
        t.addIso(this);
        testwares.add(t);
    }

    public void removeTestware() {
        testwares = Sets.newHashSet();
    }

    public void setLatestInDrop() {
        this.latestInDrop = true;
    }

    public void obsolete() {
        this.latestInDrop = false;
    }

}
