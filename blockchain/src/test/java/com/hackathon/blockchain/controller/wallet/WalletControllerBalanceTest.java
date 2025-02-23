package com.hackathon.blockchain.controller.wallet;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerBalanceTest extends BaseControllerTest {
    public final String urlBase = "/wallet/balance";

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_balance_ok() throws Exception {
        Wallet wallet = userRepository.findByUsername("test").get().getWallet();

        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet_address").value(wallet.getAddress()))
                .andExpect(jsonPath("$.cash_balance").value(wallet.getBalance()))
                .andExpect(jsonPath("$.net_worth").isNumber())
                .andExpect(jsonPath("$.assets").isMap());
    }

}