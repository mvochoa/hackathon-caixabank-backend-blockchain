package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.model.WalletKey;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WalletKeyRepository {
    public Optional<WalletKey> findByWallet(Wallet wallet) {
        return Optional.empty();
    }

    public Optional<WalletKey> findByWalletId(Long walletId) {
        return Optional.empty();
    }

    public WalletKey save(WalletKey walletKey) {
        return null;
    }
}
