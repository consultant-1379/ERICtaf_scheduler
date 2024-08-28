package com.ericsson.cifwk.taf.scheduler.infrastructure.eventbus;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 05/11/2015
 */
@Configuration
public class EventBusConfiguration {

    @Bean
    public EventBus eventBusBean() {
        return new EventBus();
    }
}
