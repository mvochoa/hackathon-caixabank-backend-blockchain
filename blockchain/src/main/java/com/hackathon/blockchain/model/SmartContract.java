package com.hackathon.blockchain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class SmartContract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long issuerWalletId;
    private String name;

    @Lob
    private String conditionExpression;
    private String action;
    private Double actionValue;
    private String status;

    @Lob
    private String digitalSignature;
}
