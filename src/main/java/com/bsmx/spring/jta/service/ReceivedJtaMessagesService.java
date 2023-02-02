package com.bsmx.spring.jta.service;

import com.bsmx.spring.jta.model.JtaEntity;
import com.bsmx.spring.jta.model.ReceivedJtaMessageEntity;
import com.bsmx.spring.jta.repository.JtaRepository;
import com.bsmx.spring.jta.repository.ReceivedJtaMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CommonsLog
@RequiredArgsConstructor
public class ReceivedJtaMessagesService {


    private final ReceivedJtaMessageRepository receivedJtaMessageRepository;


    public void saveMessage(String message) {
        log.info("Save ReceivedJtaMessageEntity in DB: " + message);
        ReceivedJtaMessageEntity receivedJtaMessageEntity = new ReceivedJtaMessageEntity();
        receivedJtaMessageEntity.setMessage(message);
        receivedJtaMessageRepository.save(receivedJtaMessageEntity);
    }

    public String getMessages() {
        List<ReceivedJtaMessageEntity> all = receivedJtaMessageRepository.findAll();
        return all.stream().map(ReceivedJtaMessageEntity::getMessage).collect(Collectors.joining(","));
    }

}
