package com.hackathon.blockchain.controller.blockchain;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlockchainControllerMineTest extends BaseControllerTest {
    public final String urlBase = "/blockchain/mine";

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    public void blockchainController_mine_ok() throws Exception {
        User user = userRepository.findByUsername("test").get();
        walletService.buyAsset(user.getId(), "BTC", 0.00001);

        mockMvc.perform(post(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Block mined: 0000")));
    }

    public void blockchainController_mine_withoutTransactions() throws Exception {
        generateWallet();
        mockMvc.perform(post(urlBase))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ No pending transactions to mine.")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}