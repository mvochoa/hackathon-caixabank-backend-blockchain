package com.hackathon.blockchain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String address;
    private Double balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<Asset> assets = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String accountStatus;

    @Column(nullable = false)
    private Double netWorth = 0.0;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL)
    private WalletKey walletKey;

    @OneToMany(mappedBy = "issuerWallet", cascade = CascadeType.ALL)
    private List<SmartContract> smartContracts;

    @OneToMany(mappedBy = "senderWallet", cascade = CascadeType.ALL)
    private List<Transaction> transactionSender;

    @OneToMany(mappedBy = "receiverWallet", cascade = CascadeType.ALL)
    private List<Transaction> transactionReceiver;
}
