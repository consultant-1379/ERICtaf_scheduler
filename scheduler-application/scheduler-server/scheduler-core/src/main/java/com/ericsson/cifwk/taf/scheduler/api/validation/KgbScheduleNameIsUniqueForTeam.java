package com.ericsson.cifwk.taf.scheduler.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueKgbScheduleNameValidator.class})
@Documented
public @interface KgbScheduleNameIsUniqueForTeam {

    String message() default "KGB+N schedule names should be unique per team";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
