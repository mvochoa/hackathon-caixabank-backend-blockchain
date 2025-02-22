package com.hackathon.blockchain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Wallet {
    private Long id;
    private String address;
    private Double balance;
    private List<Asset> assets;
    private User user;
    private String accountStatus;
    private Double netWorth;
}
