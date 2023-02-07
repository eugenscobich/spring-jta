package com.bsmx.spring.jta.jms;

import com.bsmx.spring.jta.service.ReceivedJtaMessagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@CommonsLog
@RequiredArgsConstructor
public class JmsJtaListener {

    private final ReceivedJtaMessagesService receivedJtaMessagesService;

    @JmsListener(destination = "${service.jta.jms.destination}", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(String message) {
        log.info("Received Jms message: " + message);
        receivedJtaMessagesService.saveMessage(message);
    }

}
