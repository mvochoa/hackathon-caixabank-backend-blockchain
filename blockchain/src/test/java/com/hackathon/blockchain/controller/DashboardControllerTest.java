package com.hackathon.blockchain.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DashboardControllerTest extends BaseControllerTest {
    private final String urlBase = "/api/dashboard";

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void dashboardController_healthCheck_ok() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to your dashboard, test!\nYour registered email is: test@example.com"));
    }

    @Test
    public void dashboardController_healthCheck_notAuthenticated() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("You are not authenticated"));
    }
}
