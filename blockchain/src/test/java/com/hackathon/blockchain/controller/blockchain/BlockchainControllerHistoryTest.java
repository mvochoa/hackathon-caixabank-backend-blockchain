package com.hackathon.blockchain.controller.blockchain;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlockchainControllerHistoryTest extends BaseControllerTest {
    public final String urlBase = "/blockchain";

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    public void blockchainController_history_ok() throws Exception {
        User user = userRepository.findByUsername("test").get();
        walletService.buyAsset(user.getId(), "BTC", 0.00001);
        blockchainService.mine();

        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

}