package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    Optional<Wallet> findByAddress(String address);
}
