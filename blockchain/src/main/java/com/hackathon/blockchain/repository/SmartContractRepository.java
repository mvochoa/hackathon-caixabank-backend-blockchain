package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.SmartContract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmartContractRepository extends CrudRepository<SmartContract, Long> {
    List<SmartContract> findByIssuerWalletId(Long issuerWalletId);
}
