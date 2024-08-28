package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 22/07/2015
 * Exception aggregate for ParserConfigurationException | SAXException | IOException cases
 */
public class ParserException extends RuntimeException {

    public ParserException(Throwable cause) {
        super(cause);
    }
}
