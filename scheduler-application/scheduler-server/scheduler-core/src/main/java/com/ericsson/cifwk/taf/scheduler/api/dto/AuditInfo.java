package com.ericsson.cifwk.taf.scheduler.api.dto;

import java.util.Date;

/**
 * Created by evlailj on 16/06/2015.
 */
public class AuditInfo {

    private Date created;

    private String createdBy;

    private Date updated;

    private String updatedBy;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
