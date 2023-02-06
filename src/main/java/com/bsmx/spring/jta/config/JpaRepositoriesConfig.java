package com.bsmx.spring.jta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "localTransactionManager",
        basePackages = {"com.bsmx.spring.jta.repository"})
public class JpaRepositoriesConfig {
}
