package com.ericsson.cifwk.taf.scheduler.infrastructure.security;

import com.ericsson.cifwk.taf.scheduler.infrastructure.security.login.HttpAuthenticationEntryPoint;
import com.ericsson.cifwk.taf.scheduler.infrastructure.security.login.HttpLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

    protected static final String LOGIN_API = "/api/login";
    protected static final String API_SCHEDULES = "/api/schedules/**";
    protected static final String API_SCHEDULE_EXECUTIONS = "/api/schedules/executions";
    private static final String[] API_OPEN = {
        "/api/swagger-ui.html",
        "/api/webjars/springfox-swagger-ui/**",
        "/api/swagger-resources",
        "/api/v2/api-docs",
        "/api/hystrix.stream",
        "/api/application"
    };

    @Autowired
    private HttpLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGIN_API, "DELETE"))
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .sessionManagement()
                //only 1 session per user
                .maximumSessions(1);

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, API_SCHEDULES).permitAll()
                .antMatchers(HttpMethod.POST, API_SCHEDULE_EXECUTIONS).permitAll()
                .antMatchers(API_OPEN).permitAll()
                .antMatchers(LOGIN_API).permitAll()
                .anyRequest().authenticated();
    }

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
