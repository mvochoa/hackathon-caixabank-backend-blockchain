package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.SmartContract;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartContractRepository {
    public List<SmartContract> findAll() {
        return List.of();
    }
}
