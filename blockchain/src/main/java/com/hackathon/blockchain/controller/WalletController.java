package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.WalletKeyDto;
import com.hackathon.blockchain.dto.wallet.BuyAssetWalletDto;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.model.WalletKey;
import com.hackathon.blockchain.repository.UserRepository;
import com.hackathon.blockchain.service.WalletKeyService;
import com.hackathon.blockchain.service.WalletService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static com.hackathon.blockchain.model.Constants.KEYS_FOLDER;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

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
    public ResponseEntity<ResponseMessageDto> buy(@AuthenticationPrincipal User user, @Valid @RequestBody BuyAssetWalletDto buyAssetWalletDto) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(walletService.buyAsset(user.getId(), buyAssetWalletDto.getSymbol(), buyAssetWalletDto.getQuantity()))
                        .build(),
                HttpStatus.OK);
    }

}