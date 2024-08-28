package com.ericsson.oss.axis.interfaces.scheduler.exceptions;

public class TafSchedulerException extends Exception {

    public TafSchedulerException(String message) {
        super(message);
    }

    public TafSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TafSchedulerException(Throwable cause) {
        super(cause);
    }
}
