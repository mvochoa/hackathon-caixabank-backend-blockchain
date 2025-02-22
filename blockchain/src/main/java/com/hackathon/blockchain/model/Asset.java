package com.hackathon.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private Long id;
    private String symbol;
    private Double quantity;
    private Double current;
    private Wallet wallet;
}
