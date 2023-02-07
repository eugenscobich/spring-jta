package com.bsmx.spring.jta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
@RequiredArgsConstructor
public class JmsService {

    private final JmsTemplate xaJmsTemplate;

    private final JmsTemplate simpleJmsTemplate;

    @Value("${service.jta.jms.destination}")
    private String detination;

    public void sendJmsMessage(String message) {
        log.info("Send message to Jms: " + message);
        xaJmsTemplate.convertAndSend(detination, message);
    }

    public void simpleSendJmsMessage(String message) {
        log.info("Send message to Jms: " + message);
        simpleJmsTemplate.convertAndSend(detination, message);
    }

}
