package com.ericsson.cifwk.taf.scheduler.api.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         05/11/2015
 */
public class CommentInfo extends AuditInfo {

    @NotBlank
    private String message;

    private int scheduleVersion;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getScheduleVersion() {
        return scheduleVersion;
    }

    public void setScheduleVersion(int scheduleVersion) {
        this.scheduleVersion = scheduleVersion;
    }
}
