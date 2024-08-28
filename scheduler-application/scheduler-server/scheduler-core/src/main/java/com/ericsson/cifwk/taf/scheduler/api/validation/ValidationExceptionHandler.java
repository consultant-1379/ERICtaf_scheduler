package com.ericsson.cifwk.taf.scheduler.api.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<String> handle(ConstraintViolationException e) { //NOSONAR
        return e.getConstraintViolations().stream()
                .map(c -> c.getMessage())
                .collect(toList());
    }
}
