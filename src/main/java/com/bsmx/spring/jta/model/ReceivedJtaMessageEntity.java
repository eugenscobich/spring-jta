package com.bsmx.spring.jta.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "received_jta_messages")
@Setter
@Getter
public class ReceivedJtaMessageEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column
    private String message;


}
