package com.bsmx.spring.jta.service;

import com.bsmx.spring.jta.repository.JtaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
@RequiredArgsConstructor
public class JmsService {

    private final JmsTemplate jmsTemplate;

    @Value("${service.jta.jms.destination}")
    private String detination;

    public void sendJmsMessage(String message) {
        log.info("Send message to Jms: " + message);
        jmsTemplate.convertAndSend(detination, message);
    }

}