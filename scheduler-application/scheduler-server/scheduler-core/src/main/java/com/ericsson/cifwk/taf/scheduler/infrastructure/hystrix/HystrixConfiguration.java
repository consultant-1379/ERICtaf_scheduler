package com.ericsson.cifwk.taf.scheduler.infrastructure.hystrix;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 13/07/2015
 */
@Configuration
public class HystrixConfiguration {

    @Bean
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    @PostConstruct
    public void configureHystrix() {
        HystrixPlugins.getInstance().registerCommandExecutionHook(new HystrixCommandHook());
    }

    @PreDestroy
    public void resetHystrix() {
        Hystrix.reset();
    }
}
