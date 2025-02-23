package com.hackathon.blockchain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "issuer_wallet_id", insertable = false, updatable = false)
    private Long issuerWalletId;
    private String name;
    private String conditionExpression;
    private String action;
    private Double actionValue;
    private String digitalSignature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_wallet_id")
    private Wallet issuerWallet;
}
