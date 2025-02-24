package com.hackathon.blockchain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockchainDto {
    private Long id;
    private Long blockIndex;
    private Long timestamp;
    private String previousHash;
    private Long nonce;
    private String hash;
    private Boolean genesis;
}
