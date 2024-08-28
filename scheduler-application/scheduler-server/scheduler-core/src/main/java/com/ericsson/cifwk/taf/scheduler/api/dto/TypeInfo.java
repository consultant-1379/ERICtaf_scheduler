package com.ericsson.cifwk.taf.scheduler.api.dto;

public class TypeInfo {

    private Integer id;

    private String name;

    public TypeInfo() {
        // Empty Constructor required
    }

    public TypeInfo(int id) {
        this(id, null);
    }

    public TypeInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
