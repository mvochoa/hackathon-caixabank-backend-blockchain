package com.hackathon.blockchain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmartContract {
    private Long issuerWalletId;
    private String name;
    private String conditionExpression;
    private String action;
    private Double actionValue;
    private String digitalSignature;
}
