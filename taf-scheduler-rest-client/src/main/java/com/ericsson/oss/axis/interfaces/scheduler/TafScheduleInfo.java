package com.ericsson.oss.axis.interfaces.scheduler;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TafScheduleInfo {

    private Long id;
    private String name;
    //TODO: resolve problem with deserialization to get schedule type (may come in handy at some stage) (ScheduleType type;)
    private Integer version;
    private List<Integer> versionList;
    private boolean lastVersion;
    private String xml;
    private String xmlContent;
    private String url;

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

    public List<Integer> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Integer> versionList) {
        this.versionList = versionList;
    }

    public boolean isLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getXml() {
        // We get 'xmlContent' instead of 'xml' when using GET /api/schedules/{scheduleId}.
        // xmlContent doesn't need a setter as Retrofit powered by Gson sets instance fields only directly
        return StringUtils.isBlank(xmlContent) ? xml : xmlContent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TafScheduleInfo that = (TafScheduleInfo) o;

        if (lastVersion != that.lastVersion) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }
        if (versionList != null ? !versionList.equals(that.versionList) : that.versionList != null) {
            return false;
        }
        if (xml != null ? !xml.equals(that.xml) : that.xml != null) {
            return false;
        }
        if (xmlContent != null ? !xmlContent.equals(that.xmlContent) : that.xmlContent != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (versionList != null ? versionList.hashCode() : 0);
        result = 31 * result + (lastVersion ? 1 : 0);
        result = 31 * result + (xml != null ? xml.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TafScheduleInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", versionList=" + versionList +
                ", lastVersion=" + lastVersion +
                ", xml='" + xml + '\'' +
                ", xmlContent='" + xmlContent + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
