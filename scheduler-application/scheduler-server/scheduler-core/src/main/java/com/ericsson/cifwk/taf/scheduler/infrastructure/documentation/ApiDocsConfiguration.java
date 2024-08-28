package com.ericsson.cifwk.taf.scheduler.infrastructure.documentation;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class ApiDocsConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(apiPaths())
                .build();
    }

    private Predicate<String> apiPaths() {
        return regex("/api/.*");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("TAF Scheduler API living documentation")
                .description("This page provides living API documentation for TAF Scheduler." +
                             "You are able to make calls to API from this page")
                .contact("TAF team")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //allow to get swagger ui html under 'api/', on the classpath
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        //allow swagger ui to access resources under /webjars/springfox-swagger-ui/**
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
