package com.ericsson.cifwk.taf.scheduler.infrastructure.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {

    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.poolName}")
    private String poolName;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Autowired
    private ObjectFactory<DataSource> dataSourceProvider;

    @Bean
    LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceProvider.getObject());
        return sessionFactory;
    }

    @Bean
    DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    private HikariConfig hikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(poolName);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setDataSourceClassName(driverClassName);
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setIdleTimeout(32000);

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("url", url);
        dataSourceProperties.setProperty("user", username);
        dataSourceProperties.setProperty("password", password);
        dataSourceProperties.setProperty("cachePrepStmts", "true");
        dataSourceProperties.setProperty("prepStmtCacheSize", "250");
        dataSourceProperties.setProperty("prepStmtCacheSqlLimit", "2048");
        dataSourceProperties.setProperty("useServerPrepStmts", "true");

        hikariConfig.setDataSourceProperties(dataSourceProperties);

        return hikariConfig;
    }
}
