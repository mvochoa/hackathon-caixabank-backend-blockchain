package com.hackathon.blockchain.controller.market;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarketControllerPriceTest extends BaseControllerTest {
    public final String urlBase = "/market/price/{symbol}";

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void marketController_prices_ok() throws Exception {
        mockMvc.perform(get(urlBase, "BTC"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Current price of ")));
    }

    @Test
    public void marketController_prices_symbolInvalid() throws Exception {
        mockMvc.perform(get(urlBase, "BTC1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Asset not found or price unavailable: BTC1")
                                .build()),
                        JsonCompareMode.STRICT));
    }
}