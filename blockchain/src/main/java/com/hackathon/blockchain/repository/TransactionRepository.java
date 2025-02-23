package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Transaction;
import com.hackathon.blockchain.model.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByStatus(String status);

    List<Transaction> findBySenderWallet(Wallet wallet);

    List<Transaction> findByReceiverWallet(Wallet wallet);
}
