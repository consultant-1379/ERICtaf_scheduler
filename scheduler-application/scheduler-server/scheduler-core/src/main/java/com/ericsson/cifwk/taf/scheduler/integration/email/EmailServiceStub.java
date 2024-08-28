/*
 * COPYRIGHT Ericsson (c) 2014.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.cifwk.taf.scheduler.integration.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@Profile({"dev", "itest"})
public class EmailServiceStub implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceStub.class);

    @Override
    public void sendEmail(List<String> recipients, String subject, String text) {
        LOG.info("Sent email:\nTo: {}\nSubj: {}\nText: {}", recipients, subject, text);
    }
}
