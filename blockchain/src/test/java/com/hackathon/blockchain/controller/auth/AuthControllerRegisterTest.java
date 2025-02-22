package com.hackathon.blockchain.controller.auth;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.auth.RegisterUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerRegisterTest extends BaseAuthControllerTest {
    public final String urlBase = "/auth/register";
    public final RegisterUserDto data = RegisterUserDto.builder()
            .email(user.getEmail())
            .username(user.getUsername())
            .password(userPassword)
            .build();

    @Test
    public void authController_register_ok() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("User registered and logged in successfully")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    public void authController_register_usernameAlreadyExists() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Username already exists")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}