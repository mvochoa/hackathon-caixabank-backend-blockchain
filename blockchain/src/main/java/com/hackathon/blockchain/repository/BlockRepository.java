package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.Block;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends PagingAndSortingRepository<Block, Long>, CrudRepository<Block, Long> {
    Optional<Block> findFirstByOrderByIdDesc();
}
