package com.ericsson.cifwk.taf.scheduler;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final String DEFAULT_ENV = "dev";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        String env = System.getProperty("env", DEFAULT_ENV);
        LOG.info("Environment set to {}", env);
        application.profiles(env);
        return application.sources(Application.class);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/api/hystrix.stream");
    }

    @Bean
    public ValidatorFactory validatorFactory() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public Validator validator() {
        return validatorFactory().getValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor mvpp = new MethodValidationPostProcessor();
        mvpp.setValidator(validator());

        return mvpp;
    }
}
