package com.bsmx.spring.jta.service;

import com.bsmx.spring.jta.model.JtaEntity;
import com.bsmx.spring.jta.repository.JtaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CommonsLog
@RequiredArgsConstructor
public class JtaService {


    private final JtaRepository jtaRepository;


    public void saveMessage(String message) {
        log.info("Save message in DB: " + message);
        JtaEntity jtaEntity = new JtaEntity();
        jtaEntity.setMessage(message);
        jtaRepository.save(jtaEntity);
    }

    public String getMessages() {
        List<JtaEntity> all = jtaRepository.findAll();
        Stream<String> messagesStream = all.stream().map(JtaEntity::getMessage);
        Stream<String> largeMessagesStream = all.stream().map(JtaEntity::getMessage);
        return Stream.concat(messagesStream, largeMessagesStream).collect(Collectors.joining(","));
    }

    public void saveLargeMessage(String largeMessage) {
        log.info("Save large message in DB: " + largeMessage);
        JtaEntity jtaEntity = new JtaEntity();
        jtaEntity.setLargeMessage(largeMessage);
        jtaRepository.save(jtaEntity);
    }
}
