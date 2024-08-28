package com.ericsson.cifwk.taf.scheduler.model;

import com.google.common.collect.Sets;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "ProductDrop")
@SQLInsert(sql = "INSERT IGNORE INTO ProductDrop(name, productId) VALUES(?, ?)")
public class Drop extends AbstractPersistable<Long> implements Serializable {

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "drops", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<ISO> isos = Sets.newHashSet();

    public Drop() {
        // Empty constructor required
    }

    public Drop(Product product, String name) {
        this.product = product;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void addIso(ISO iso) {
        isos.add(iso);
    }

    public Set<ISO> getIsos() {
        return Collections.unmodifiableSet(isos);
    }

    public void setIsos(Set<ISO> isos) {
        this.isos = isos;
    }
}
