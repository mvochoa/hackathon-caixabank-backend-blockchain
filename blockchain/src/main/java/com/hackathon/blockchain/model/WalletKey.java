package com.hackathon.blockchain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletKey {
    private Wallet wallet;
    private String publicKey;
    private String privateKey;
}
