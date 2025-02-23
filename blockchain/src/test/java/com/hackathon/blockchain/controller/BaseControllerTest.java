package com.hackathon.blockchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.blockchain.model.Asset;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.repository.AssetRepository;
import com.hackathon.blockchain.repository.BlockRepository;
import com.hackathon.blockchain.repository.SmartContractRepository;
import com.hackathon.blockchain.repository.TransactionRepository;
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

    @Autowired
    protected BlockRepository blockRepository;

    @Autowired
    protected SmartContractRepository smartContractRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    protected final String userPassword = "password";
    protected final User user = User.builder()
            .email("test@example.com")
            .username("test")
            .build();

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        smartContractRepository.deleteAll();
        walletKeyRepository.deleteAll();
        walletRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();
        blockRepository.deleteAll();
        walletService.initializeLiquidityPools();
        userRepository.save(user.toBuilder().password(passwordEncoder.encode(userPassword)).build());
    }

    public Wallet generateWallet() {
        String address = walletService.createWalletForUser(Objects.requireNonNull(userRepository.findByUsername("test").orElse(null)));
        Wallet wallet = walletRepository.findByAddress(address.replace("✅ Wallet successfully created! Address: ", "")).orElse(null);

        Asset asset = Asset.builder().symbol("USDT").quantity(1000.0).wallet(wallet).build();
        assetRepository.save(asset);
        assetRepository.save(asset.toBuilder().id(null).symbol("BTC").build());

        return walletRepository.findById(wallet.getId()).get();
    }

}
