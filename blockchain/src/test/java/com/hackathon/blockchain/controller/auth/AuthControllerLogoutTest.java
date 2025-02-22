package com.hackathon.blockchain.controller.auth;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerLogoutTest extends BaseAuthControllerTest {
    public final String urlBase = "/auth/logout";

    @Test
    public void authController_logout_ok() throws Exception {
        mockMvc.perform(post(urlBase)
                        .sessionAttr("username", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Logged out successfully")
                                .build()),
                        JsonCompareMode.STRICT));
    }
}