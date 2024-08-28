package com.ericsson.cifwk.taf.scheduler.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "Product")
public class Product extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "product")
    private Set<Drop> drops;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Drop> getDrops() {
        return drops;
    }

    public void setDrops(Set<Drop> drops) {
        this.drops = drops;
    }
}
