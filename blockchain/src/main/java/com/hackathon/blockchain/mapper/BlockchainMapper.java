package com.hackathon.blockchain.mapper;

import com.hackathon.blockchain.dto.BlockchainDto;
import com.hackathon.blockchain.model.Block;

public class BlockchainMapper {
    public static BlockchainDto entityToDto(Block block) {
        return BlockchainDto.builder()
                .id(block.getId())
                .blockIndex(block.getBlockIndex())
                .timestamp(block.getTimestamp())
                .previousHash(block.getPreviousHash())
                .nonce(block.getNonce())
                .hash(block.getHash())
                .genesis(block.getIsGenesis())
                .build();
    }
}
