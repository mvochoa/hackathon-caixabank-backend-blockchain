package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Asset;
import com.hackathon.blockchain.model.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {
    Optional<Asset> findByWallet(Wallet wallet);
}
