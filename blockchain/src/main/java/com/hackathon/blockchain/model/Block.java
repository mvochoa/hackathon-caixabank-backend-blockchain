package com.hackathon.blockchain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Block {
    private String calculateHash;
    private String hash;
    private String blockIndex;
    private String previousHash;
}
