package com.hackathon.blockchain.controller.auth;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.auth.LoginUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerLoginTest extends BaseControllerTest {
    public final String urlBase = "/auth/login";
    public final LoginUserDto data = LoginUserDto.builder()
            .username(user.getUsername())
            .password(userPassword)
            .build();

    @Test
    public void authController_login_ok() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Login successful")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    public void authController_login_usernameNotExists() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().username("other").build())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Invalid credentials")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    public void authController_login_passwordIncorrect() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().password("invalid").build())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Invalid credentials")
                                .build()),
                        JsonCompareMode.STRICT));
    }
}