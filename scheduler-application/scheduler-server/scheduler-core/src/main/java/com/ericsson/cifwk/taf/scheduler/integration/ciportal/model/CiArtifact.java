package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

public class CiArtifact {

    private String group;
    private String name;
    private String version;

    private String mediaCategory;
    private String mediaPath;
    private String number;
    private String platform;
    private String url;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getMediaCategory() {
        return mediaCategory;
    }

    public void setMediaCategory(String mediaCategory) {
        this.mediaCategory = mediaCategory;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
