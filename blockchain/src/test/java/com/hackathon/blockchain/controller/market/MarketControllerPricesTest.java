package com.hackathon.blockchain.controller.market;

import com.hackathon.blockchain.controller.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarketControllerPricesTest extends BaseControllerTest {
    public final String urlBase = "/market/prices";

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void marketController_prices_ok() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.*").isArray());
    }
}