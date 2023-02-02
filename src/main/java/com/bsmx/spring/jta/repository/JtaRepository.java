package com.bsmx.spring.jta.repository;

import com.bsmx.spring.jta.model.JtaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JtaRepository extends JpaRepository<JtaEntity, Long> {
}
