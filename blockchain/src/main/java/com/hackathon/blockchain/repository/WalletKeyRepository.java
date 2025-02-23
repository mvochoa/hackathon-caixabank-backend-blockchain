package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.model.WalletKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletKeyRepository extends CrudRepository<WalletKey, Long> {
    Optional<WalletKey> findByWallet(Wallet wallet);

    Optional<WalletKey> findByWalletId(Long walletId);
}
