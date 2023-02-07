package com.bsmx.spring.jta.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableConfigurationProperties(ActiveMQProperties.class)
public class JMSConfig {


    @Bean
    JmsTemplate simpleJmsTemplate(ActiveMQProperties properties) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory(properties));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(ActiveMQProperties properties){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(properties.getBrokerUrl());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setUserName(properties.getUser());
        return connectionFactory;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQProperties properties) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory(properties));
        factory.setConcurrency("1-1");
        return factory;
    }

}
