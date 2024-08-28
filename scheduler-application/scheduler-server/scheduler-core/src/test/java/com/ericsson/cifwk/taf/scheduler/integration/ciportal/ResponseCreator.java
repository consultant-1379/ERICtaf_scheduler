package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.response.DefaultResponseCreator;

public class ResponseCreator extends DefaultResponseCreator {

    protected ResponseCreator(HttpStatus statusCode) {
        super(statusCode);
    }

    public static ResponseCreator withNotFound() {
        return new ResponseCreator(HttpStatus.NOT_FOUND);
    }

}
