package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Asset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {
}
