package com.ericsson.cifwk.taf.scheduler.api.dto;

public class SimpleScheduleInfo {

    private Long id;

    private String name;

    private Integer version;

    private TypeInfo type;

    private String createdBy;

    private String xml;

    private String url;

    private boolean lastVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setType(TypeInfo type) {
        this.type = type;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(boolean lastVersion) {
        this.lastVersion = lastVersion;
    }
}
