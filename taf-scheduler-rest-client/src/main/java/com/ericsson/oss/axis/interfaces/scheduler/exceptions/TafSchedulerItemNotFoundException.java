package com.ericsson.oss.axis.interfaces.scheduler.exceptions;

public class TafSchedulerItemNotFoundException extends TafSchedulerException {

    public static final String FAILED_TO_FIND_DEFINED_SCHEDULE =
            "Failed to find the defined schedule in TAF Scheduler - it's either not there or not approved. " +
                    "Use verification on the configuration page to check schedule availability.";

    public TafSchedulerItemNotFoundException() {
        super(FAILED_TO_FIND_DEFINED_SCHEDULE);
    }
}
