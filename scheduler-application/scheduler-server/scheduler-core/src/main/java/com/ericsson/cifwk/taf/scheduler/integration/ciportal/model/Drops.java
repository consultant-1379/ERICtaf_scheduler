package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Drops {

    @JsonProperty("Drops")
    private List<String> rawDrops;

    public Drops() {
        // Empty constructor required
    }

    /**
     * Drops are returned as "Product:CiDrop" and require additional formatting
     *
     * @return list of drops
     */
    public List<String> getRawDrops() {
        return rawDrops;
    }

    public void setRawDrops(List<String> rawDrops) {
        this.rawDrops = rawDrops;
    }

}
