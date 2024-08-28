package com.ericsson.cifwk.taf.scheduler.model;

import com.google.common.collect.Sets;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "Iso")
public class ISO extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "latestInDrop")
    private boolean latestInDrop;

    @ManyToMany
    @JoinTable(
            name = "IsoDrops",
            joinColumns = {@JoinColumn(name = "isoId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "dropId", referencedColumnName = "id")})
    private Set<Drop> drops = Sets.newHashSet();

    public ISO() {
        // Empty constructor required
    }

    public ISO(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public ISO(String name, String version, Drop drop) {
        this.name = name;
        this.version = version;
        addDrop(drop);
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

    public Set<Drop> getDrops() {
        return drops;
    }

    public void setDrops(Set<Drop> drops) {
        this.drops = drops;
    }

    public void addDrop(Drop d) {
        d.addIso(this);
        drops.add(d);
    }

    public boolean isLatestInDrop() {
        return latestInDrop;
    }

    public void setLatestInDrop() {
        this.latestInDrop = true;
    }

    public void obsolete() {
        this.latestInDrop = false;
    }
}
