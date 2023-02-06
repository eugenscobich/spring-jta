package com.bsmx.spring.jta.xa.repository;

import com.bsmx.spring.jta.model.JtaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JtaRepository extends JpaRepository<JtaEntity, Long> {
}
