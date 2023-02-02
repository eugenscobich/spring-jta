package com.bsmx.spring.jta.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jta")
@Setter
@Getter
public class JtaEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(length = 36)
    private String message;

    @Column
    private String largeMessage;


}
