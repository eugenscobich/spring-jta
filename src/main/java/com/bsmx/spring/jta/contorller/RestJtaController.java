package com.bsmx.spring.jta.contorller;

import com.bsmx.spring.jta.service.JmsService;
import com.bsmx.spring.jta.service.JtaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RestJtaController {

    private final JmsService jmsService;
    private final JtaService jtaService;

    @PostMapping(path = "/sendJmsMessage")
    @Transactional
    public String sendJmsMessage(@RequestBody Map<String, Object> map) {
        Object messageFromMap = map.get("message");
        if (messageFromMap != null) {
            String message = messageFromMap.toString();
            jmsService.sendJmsMessage(message);
            jtaService.saveMessage(message);
        }
        Object largeMessageFromMap = map.get("largeMessage");
        if (largeMessageFromMap != null) {
            String largeMessage = largeMessageFromMap.toString();
            jmsService.sendJmsMessage(largeMessage);
            jtaService.saveLargeMessage(largeMessage);
        }
        if (map.get("errorAtEnd") != null && map.get("errorAtEnd").equals(true)) {
            throw new IllegalStateException("Error at the end");
        }
        return "OK";
    }

    @GetMapping(path = "/getMessages")
    public String getMessages() {
        return jtaService.getMessages();
    }


}
