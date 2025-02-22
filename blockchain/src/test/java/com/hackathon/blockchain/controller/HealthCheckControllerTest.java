package com.hackathon.blockchain.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckControllerTest extends BaseControllerTest {
    private final String urlBase = "/health";

    @Test
    public void healthCheckController_healthCheck_ReturnResponseOk() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().string("API is working"));
    }
}
