package com.bsmx.spring.jta.xa.repository;

import com.bsmx.spring.jta.model.JtaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XaJtaRepository extends JpaRepository<JtaEntity, Long> {
}
