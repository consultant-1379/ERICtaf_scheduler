package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import org.springframework.stereotype.Component;

import javax.xml.validation.Schema;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 21/07/2015
 */

@Component
public class ScheduleValidatorFactory {

    public ScheduleValidator create(Schema schema) {
        return new ScheduleValidator(schema);
    }
}
