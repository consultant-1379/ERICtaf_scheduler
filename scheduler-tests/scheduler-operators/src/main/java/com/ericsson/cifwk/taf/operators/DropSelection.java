package com.ericsson.cifwk.taf.operators;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 23/07/2015
 */
public class DropSelection {

    private String drop;
    private String product;

    public DropSelection(String product, String drop) {
        this.drop = drop;
        this.product = product;
    }

    public String getDrop() {
        return drop;
    }

    public String getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DropSelection that = (DropSelection) o;
        return java.util.Objects.equals(drop, that.drop) &&
                java.util.Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(drop, product);
    }
}
