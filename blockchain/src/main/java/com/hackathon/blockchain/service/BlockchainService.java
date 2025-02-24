package com.hackathon.blockchain.service;

import com.hackathon.blockchain.dto.BlockchainDto;
import com.hackathon.blockchain.mapper.BlockchainMapper;
import com.hackathon.blockchain.model.Block;
import com.hackathon.blockchain.model.Transaction;
import com.hackathon.blockchain.repository.BlockRepository;
import com.hackathon.blockchain.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BlockchainService {

    private final TransactionRepository transactionRepository;
    private final BlockRepository blockRepository;
    private final SmartContractEvaluationService smartContractEvaluationService;

    public boolean isChainValid() {
        List<Block> chain = (List<Block>) blockRepository.findAll(Sort.by(Sort.Direction.ASC, "blockIndex"));

        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            String hash = current.getHash();
            String recalculatedHash = current.getCalculateHash();
            if (!hash.equals(recalculatedHash)) {
                System.out.println("❌ Hash mismatch in block " + current.getBlockIndex());
                System.out.println("Stored hash: " + hash);
                System.out.println("Recalculated: " + recalculatedHash);
                return false;
            }

            if (!current.getPreviousHash().equals(previous.getHash())) {
                System.out.println("❌ Previous hash mismatch in block " + current.getBlockIndex());
                return false;
            }
        }

        System.out.println("✅ Blockchain is valid");
        return true;
    }

    public String mine() {
        Block previousBlock = blockRepository.findFirstByOrderByIdDesc().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "❌ No exist previous block."));
        List<Transaction> pendingTxs = transactionRepository.findByStatus("PENDING");
        if (pendingTxs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "❌ No pending transactions to mine.");
        }

        Block block = Block.builder()
                .blockIndex(previousBlock.getBlockIndex() + 1)
                .isGenesis(false)
                .previousHash(previousBlock.getHash())
                .timestamp(new Date().getTime())
                .build();

        block.getCalculateHash();
        Block newBlock = blockRepository.save(block);
        pendingTxs.forEach(x -> transactionRepository.save(x.toBuilder().status("MINED").block(newBlock).build()));
        return String.format("Block mined: %s", block.getHash());
    }

    public List<BlockchainDto> history() {
        List<BlockchainDto> blockchain = new ArrayList<>();
        for (Block block : blockRepository.findAll()) {
            blockchain.add(BlockchainMapper.entityToDto(block));
        }
        return blockchain;
    }

    public void validateTransaction(Long transactionId, String symbol) {
        smartContractEvaluationService.evaluateSmartContracts();
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent() && transaction.get().getStatus().equals("CANCELED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("❌ Transaction blocked by smart contract conditions for %s", symbol));
        }
    }
}