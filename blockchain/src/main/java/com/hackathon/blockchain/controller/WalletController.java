package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.BuyAndSellAssetWalletDto;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.WalletKeyDto;
import com.hackathon.blockchain.dto.transaction.HistoryTransactionDto;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.model.WalletKey;
import com.hackathon.blockchain.repository.UserRepository;
import com.hackathon.blockchain.service.SmartContractEvaluationService;
import com.hackathon.blockchain.service.WalletKeyService;
import com.hackathon.blockchain.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {
    @Value("${app.keys.dir}")
    private String KEYS_FOLDER;

    private final SmartContractEvaluationService smartContractEvaluationService;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final WalletKeyService walletKeyService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessageDto> create(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(walletService.createWalletForUser(user))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/generate-keys")
    public ResponseEntity<WalletKeyDto> generateKeys(@AuthenticationPrincipal User user) throws NoSuchAlgorithmException, IOException {
        WalletKey walletKey = walletKeyService.generateAndStoreKeys(userRepository.findById(user.getId()).get().getWallet());

        return new ResponseEntity<>(
                WalletKeyDto.builder()
                        .message(String.format("Keys generated/retrieved successfully for wallet id: %d", walletKey.getWallet().getId()))
                        .publicKey(walletKey.getPublicKey())
                        .absolutePath(Path.of(KEYS_FOLDER).toAbsolutePath().toString())
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/buy")
    public ResponseEntity<ResponseMessageDto> buy(@AuthenticationPrincipal User user, @Valid @RequestBody BuyAndSellAssetWalletDto buyAndSellAssetWalletDto) {
        smartContractEvaluationService.evaluateSmartContracts();

        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(walletService.buyAsset(user.getId(), buyAndSellAssetWalletDto.getSymbol(), buyAndSellAssetWalletDto.getQuantity()))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/sell")
    public ResponseEntity<ResponseMessageDto> sell(@AuthenticationPrincipal User user, @Valid @RequestBody BuyAndSellAssetWalletDto buyAndSellAssetWalletDto) {
        smartContractEvaluationService.evaluateSmartContracts();

        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(walletService.sellAsset(user.getId(), buyAndSellAssetWalletDto.getSymbol(), buyAndSellAssetWalletDto.getQuantity()))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> balance(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                walletService.getWalletBalance(user.getId()),
                HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<HistoryTransactionDto> transactions(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                walletService.getWalletTransactionsByUer(user.getId()),
                HttpStatus.OK);
    }

}