package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Transaction;
import com.hackathon.blockchain.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {
    public List<Transaction> findByStatus(String status) {
        return List.of();
    }

    public List<Transaction> findBySenderWallet(Wallet wallet) {
        return List.of();
    }

    public List<Transaction> findByReceiverWallet(Wallet wallet) {
        return List.of();
    }

    public void save(Transaction transaction) {
    }
}
