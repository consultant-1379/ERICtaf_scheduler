package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */
public class ScheduleErrorInfo {

    private ErrorRange errorRange;
    private String message;

    public ScheduleErrorInfo() {
        // Empty Constructor required
    }

    public ScheduleErrorInfo(ErrorRange errorRange, String message) {
        this.errorRange = errorRange;
        this.message = message;
    }

    public ErrorRange getErrorRange() {
        return errorRange;
    }

    public void setErrorRange(ErrorRange errorRange) {
        this.errorRange = errorRange;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleErrorInfo that = (ScheduleErrorInfo) o;
        return Objects.equal(errorRange, that.errorRange) &&
                Objects.equal(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(errorRange, message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errorRange", errorRange)
                .add("message", message)
                .toString();
    }
}
