package com.bsmx.spring.jta.service;

import com.bsmx.spring.jta.model.JtaEntity;
import com.bsmx.spring.jta.repository.JtaRepository;
import com.bsmx.spring.jta.xa.repository.XaJtaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CommonsLog
@RequiredArgsConstructor
public class JtaService {


    private final XaJtaRepository xaJtaRepository;
    private final JtaRepository jtaRepository;

    public void saveMessage(String message) {
        log.info("Save message in DB: " + message);
        JtaEntity jtaEntity = new JtaEntity();
        xaJtaRepository.save(jtaEntity);
        Optional<JtaEntity> jtaEntityOptional = xaJtaRepository.findById(jtaEntity.getId());
        jtaEntityOptional.ifPresent(e -> e.setMessage(message));
    }

    public void simpleSaveMessage(String message) {
        log.info("Save message in DB: " + message);
        JtaEntity jtaEntity = new JtaEntity();
        jtaRepository.save(jtaEntity);
        Optional<JtaEntity> jtaEntityOptional = jtaRepository.findById(jtaEntity.getId());
        jtaEntityOptional.ifPresent(e -> e.setMessage(message));
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
        xaJtaRepository.save(jtaEntity);
    }
}
