package com.potato.balbambalbam.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.*;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.potato.balbambalbam.data.repository",
        entityManagerFactoryRef = "testEntityManagerFactory",
        transactionManagerRef = "testTransactionManager"
)
public class TestDataSourceConfig {

    @Primary
    @Bean(name = "testDataSource")
    @ConfigurationProperties("spring.datasource.test")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "testEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean testEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("testDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.potato.balbambalbam.data.entity")
                .persistenceUnit("test")
                .properties(hibernateProperties())
                .build();
    }

    @Primary
    @Bean(name = "testTransactionManager")
    public PlatformTransactionManager testTransactionManager(
            @Qualifier("testEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.format_sql", "true");
        return properties;
    }
}