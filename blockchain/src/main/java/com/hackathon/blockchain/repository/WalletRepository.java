package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepository {
    public List<Wallet> findAll() {
        return List.of();
    }

    public Optional<Wallet> findByUserId(Long userId) {
        return Optional.empty();
    }

    public Optional<Wallet> findByAddress(String address) {
        return Optional.empty();
    }

    public Optional<Wallet> findById(Long id) {
        return Optional.empty();
    }

    public void save(Wallet wallet) {
    }
}
