package com.hackathon.blockchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.repository.AssetRepository;
import com.hackathon.blockchain.repository.UserRepository;
import com.hackathon.blockchain.repository.WalletKeyRepository;
import com.hackathon.blockchain.repository.WalletRepository;
import com.hackathon.blockchain.service.UserService;
import com.hackathon.blockchain.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected WalletRepository walletRepository;

    @Autowired
    protected WalletService walletService;

    @Autowired
    protected AssetRepository assetRepository;

    @Autowired
    protected WalletKeyRepository walletKeyRepository;

    protected final String userPassword = "password";
    protected final User user = User.builder()
            .email("test@example.com")
            .username("test")
            .build();

    @BeforeEach
    public void setUp() {
        walletKeyRepository.deleteAll();
        walletRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(user.toBuilder().password(passwordEncoder.encode(userPassword)).build());
    }

    public Wallet generateWallet() {
        String address = walletService.createWalletForUser(Objects.requireNonNull(userRepository.findByUsername("test").orElse(null)));
        return walletRepository.findByAddress(address.replace("✅ Wallet successfully created! Address: ", "")).orElse(null);
    }
}
