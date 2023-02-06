package com.bsmx.spring.jta.config;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.postgresql.xa.PGXADataSource;
import org.postgresql.xa.PGXADataSourceFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.autoconfigure.transaction.jta.JtaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.boot.jta.atomikos.*;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

import javax.jms.Message;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties({ AtomikosProperties.class, JtaProperties.class })
public class AtomikosTransactionManagerConfig {


    @Bean
    public DataSource xaDataSource(DataSourceProperties dataSourceProperties) {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(dataSourceProperties.determineUrl());
        pgxaDataSource.setUser(dataSourceProperties.determineUsername());
        pgxaDataSource.setPassword(dataSourceProperties.determinePassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(pgxaDataSource);
        xaDataSource.setUniqueResourceName(dataSourceProperties.determineDatabaseName());
        return xaDataSource;
    }
/*
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL(this.environment.getProperty( "jms.broker.url")  );
        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
        atomikosConnectionFactoryBean.setUniqueResourceName("xamq");
        atomikosConnectionFactoryBean.setLocalTransactionMode(false);
        atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
        return atomikosConnectionFactoryBean;
    }
*/


    @Bean(initMethod = "init", destroyMethod = "shutdownWait")
    @ConditionalOnMissingBean(UserTransactionService.class)
    UserTransactionServiceImp userTransactionService(AtomikosProperties atomikosProperties,
                                                     JtaProperties jtaProperties) {
        Properties properties = new Properties();
        if (StringUtils.hasText(jtaProperties.getTransactionManagerId())) {
            properties.setProperty("com.atomikos.icatch.tm_unique_name", jtaProperties.getTransactionManagerId());
        }
        properties.setProperty("com.atomikos.icatch.log_base_dir", getLogBaseDir(jtaProperties));
        properties.putAll(atomikosProperties.asProperties());
        return new UserTransactionServiceImp(properties);
    }

    private String getLogBaseDir(JtaProperties jtaProperties) {
        if (StringUtils.hasLength(jtaProperties.getLogDir())) {
            return jtaProperties.getLogDir();
        }
        File home = new ApplicationHome().getDir();
        return new File(home, "transaction-logs").getAbsolutePath();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(javax.transaction.TransactionManager.class)
    UserTransactionManager atomikosTransactionManager(UserTransactionService userTransactionService) {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setStartupTransactionService(false);
        manager.setForceShutdown(true);
        return manager;
    }

    //@Bean
    @ConditionalOnMissingBean(XADataSourceWrapper.class)
    AtomikosXADataSourceWrapper xaDataSourceWrapper() {
        return new AtomikosXADataSourceWrapper();
    }

    @Bean
    @ConditionalOnMissingBean
    static AtomikosDependsOnBeanFactoryPostProcessor atomikosDependsOnBeanFactoryPostProcessor() {
        return new AtomikosDependsOnBeanFactoryPostProcessor();
    }

    @Bean
    JtaTransactionManager jtaTransactionManager(UserTransaction userTransaction,
                                                TransactionManager transactionManager,
                                             ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(jtaTransactionManager));
        return jtaTransactionManager;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Message.class)
    static class AtomikosJtaJmsConfiguration {

        @Bean
        @ConditionalOnMissingBean(XAConnectionFactoryWrapper.class)
        AtomikosXAConnectionFactoryWrapper xaConnectionFactoryWrapper() {
            return new AtomikosXAConnectionFactoryWrapper();
        }

    }
}
