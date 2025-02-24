package com.hackathon.blockchain.controller.wallet;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTransactionsTest extends BaseControllerTest {
    public final String urlBase = "/wallet/transactions";

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_transactions_ok() throws Exception {
        User user = userRepository.findByUsername("test").get();
        walletService.buyAsset(user.getId(), "BTC", 0.00001);
        walletService.sellAsset(user.getId(), "USDT", 2.5);

        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent").isNotEmpty())
                .andExpect(jsonPath("$.received").isNotEmpty());
    }

    @Test
    public void walletController_transactions_userNotFound() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("User not found")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}