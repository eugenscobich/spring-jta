package com.bsmx.spring.jta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

@Slf4j
public class TestApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq";
    private static final int ACTIVEMQ_PORT = 61616;
    private static final String TCP_FORMAT = "tcp://%s:%d";
    private static final String BROKER_URL_FORMAT = "spring.activemq.broker-url=%s";
    private static final int TIMEOUT_WAITING_FOR_MESSAGES = 15;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        GenericContainer activemq = new GenericContainer(ACTIVEMQ_IMAGE)
                .withExposedPorts(ACTIVEMQ_PORT)
                .withClasspathResourceMapping("activemq.xml",
                        "/opt/activemq/conf/activemq.xml", BindMode.READ_ONLY);
        activemq.start();
        var url = String.format(TCP_FORMAT, activemq.getContainerIpAddress(), activemq.getFirstMappedPort());
        log.info("ActiveMQ URL: '{}'", url);
        var property = String.format(BROKER_URL_FORMAT, url);
        TestPropertyValues.of(property).applyTo(applicationContext.getEnvironment());
    }
}
