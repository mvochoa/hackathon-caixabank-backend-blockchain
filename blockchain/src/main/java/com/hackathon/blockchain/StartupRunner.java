package com.hackathon.blockchain;

import com.hackathon.blockchain.dto.CreateSmartContractDto;
import com.hackathon.blockchain.model.Block;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.repository.BlockRepository;
import com.hackathon.blockchain.repository.SmartContractRepository;
import com.hackathon.blockchain.repository.WalletRepository;
import com.hackathon.blockchain.service.SmartContractEvaluationService;
import com.hackathon.blockchain.service.WalletKeyService;
import com.hackathon.blockchain.service.WalletService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Component
@AllArgsConstructor
public class StartupRunner {
    private final BlockRepository blockRepository;
    private final WalletService walletService;
    private final WalletKeyService walletKeyService;
    private final WalletRepository walletRepository;
    private final SmartContractRepository smartContractRepository;
    private final SmartContractEvaluationService smartContractEvaluationService;

    @PostConstruct
    public void init() {
        walletService.initializeLiquidityPools(Map.of(
                "BTC", 100000.0, "ETH", 400000.0, "USDT", 1000000.0, "NCOIN", 10000000.0, "CCOIN", 2000000.0
        ));
        Wallet wallet = walletRepository.findByAddress("LP-BTC").get();
        if (wallet.getWalletKey() == null) {
            try {
                walletKeyService.generateAndStoreKeys(wallet);
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }

        smartContractEvaluationService.createFeeWallet();
        if (smartContractRepository.findByIssuerWalletId(wallet.getId()).isEmpty()) {
            smartContractEvaluationService.create(CreateSmartContractDto.builder()
                    .name("Contract Anti-Whale")
                    .conditionExpression("#amount > 10000")
                    .action("CANCEL_TRANSACTION")
                    .actionValue(0.0)
                    .issuerWalletId(wallet.getId())
                    .build());
        }
    }

    @PostConstruct
    public void mineBlockGenesis() {
        if (blockRepository.findFirstByOrderByIdDesc().isEmpty()) {
            Block block = Block.builder()
                    .blockIndex(0L)
                    .isGenesis(true)
                    .previousHash("0")
                    .timestamp(new Date().getTime())
                    .transactions(new ArrayList<>())
                    .build();

            block.getCalculateHash();
            blockRepository.save(block);
        }
    }
}
