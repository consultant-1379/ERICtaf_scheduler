package com.ericsson.cifwk.taf.scheduler.infrastructure.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
public class HystrixCommandHook extends HystrixCommandExecutionHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixCommandHook.class);

    @Override
    public <T> Exception onExecutionError(HystrixInvokable<T> commandInstance, Exception e) {
        if (commandInstance instanceof HystrixCommand) {
            Optional.of(((HystrixCommand) commandInstance).getCommandKey())
                    .ifPresent(t -> LOGGER.error("Command '{}' execution failed", t.name(), e));
        } else {
            LOGGER.error("Command execution failed", e);
        }

        return e;
    }
}
