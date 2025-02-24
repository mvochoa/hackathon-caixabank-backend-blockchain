package com.hackathon.blockchain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

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
    private String hash;
    private Boolean isGenesis = false;
    private Long nonce;
    private String previousHash;
    private Long timestamp;
    private Long blockIndex;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    public String getCalculateHash() {
        this.setNonce(0L);
        this.setHash("");
        String base = this.getBlockIndex() + this.getPreviousHash();

        while (!this.getHash().startsWith("0000")) {
            this.setHash(DigestUtils.sha256Hex(base + this.getNonce() + this.getTimestamp()));
            this.setNonce(this.getNonce() + 1);
        }

        return this.getHash();
    }
}
