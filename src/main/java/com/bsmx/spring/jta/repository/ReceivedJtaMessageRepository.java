package com.bsmx.spring.jta.repository;

import com.bsmx.spring.jta.model.JtaEntity;
import com.bsmx.spring.jta.model.ReceivedJtaMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivedJtaMessageRepository extends JpaRepository<ReceivedJtaMessageEntity, Long> {
}
