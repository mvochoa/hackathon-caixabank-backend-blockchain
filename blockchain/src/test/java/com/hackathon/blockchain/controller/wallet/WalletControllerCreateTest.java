package com.hackathon.blockchain.controller.wallet;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerCreateTest extends BaseControllerTest {
    public final String urlBase = "/wallet/create";

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_create_ok() throws Exception {
        mockMvc.perform(post(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("✅ Wallet successfully created! Address: ")));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_create_userHasWallet() throws Exception {
        generateWallet();
        mockMvc.perform(post(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ You already have a wallet created.")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}