package com.hackathon.blockchain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String calculateHash;
    private String hash;
    private String blockIndex;
    private String previousHash;
}
