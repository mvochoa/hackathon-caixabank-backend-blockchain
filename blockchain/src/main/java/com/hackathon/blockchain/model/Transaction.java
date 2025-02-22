package com.hackathon.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private String assetSymbol;
    private Double amount;
    private Double pricePerUnit;
    private String type;
    private Date timestamp;
    private String status;
    private Double fee;
    private String block;
}
