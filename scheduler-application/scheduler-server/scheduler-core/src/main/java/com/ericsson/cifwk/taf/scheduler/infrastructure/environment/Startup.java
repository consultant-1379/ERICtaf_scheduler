package com.ericsson.cifwk.taf.scheduler.infrastructure.environment;

import com.ericsson.cifwk.taf.scheduler.infrastructure.security.ssl.SSLCertificateValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Startup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    @Value("${ssl.skip-validation:false}")
    boolean skipSSLValidation;

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        LOGGER.info("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));

        // Disable server name check in SSL extension (not supported by CI Portal)
        System.setProperty("jsse.enableSNIExtension", "false");

        if (skipSSLValidation) {
            SSLCertificateValidation.disable();
        }
    }
}
