package com.ericsson.cifwk.taf.scheduler.infrastructure.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    @Value("#{'${flyway.locations}'.split(',')}")
    private String[] locations;

    @Autowired
    private ObjectFactory<DataSource> dataSourceProvider;

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceProvider.getObject());
        flyway.setLocations(locations);
        return flyway;
    }
}
