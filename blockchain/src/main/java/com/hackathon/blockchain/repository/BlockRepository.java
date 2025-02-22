package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Block;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockRepository {
    public List<Block> findAll(Sort sort) {
        return List.of();
    }
}
