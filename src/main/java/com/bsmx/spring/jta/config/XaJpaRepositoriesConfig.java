package com.bsmx.spring.jta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManager",
        transactionManagerRef = "jtaTransactionManager",
        basePackages = {"com.bsmx.spring.jta.xa.repository"})
public class XaJpaRepositoriesConfig {
}
