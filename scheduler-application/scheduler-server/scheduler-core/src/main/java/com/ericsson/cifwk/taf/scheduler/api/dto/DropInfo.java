package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

public class DropInfo {

    @NotNull
    private Long id;

    private String productName;

    private String name;

    public DropInfo() {
        // Empty Constructor required
    }

    public DropInfo(String productName, String name) {
        this(null, productName, name);
    }

    public DropInfo(Long id, String productName, String name) {
        this.id = id;
        this.productName = productName;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("productName", productName)
                .add("version", name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DropInfo that = (DropInfo) o;
        return new EqualsBuilder().append(this.name, that.name).append(this.productName, that.productName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(productName).toHashCode();
    }
}
