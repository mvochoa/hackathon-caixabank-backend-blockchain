package com.hackathon.blockchain.controller.auth;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.auth.SessionDto;
import com.hackathon.blockchain.dto.auth.SessionUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerCheckSessionTest extends BaseControllerTest {
    public final String urlBase = "/auth/check-session";
    public final SessionDto data = SessionDto.builder()
            .user(SessionUserDto.builder()
                    .username(user.getUsername())
                    .build())
            .build();

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void authController_checkSession_ok() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(data),
                        JsonCompareMode.STRICT));
    }

    @Test
    public void authController_checkSession_sessionNotExists() throws Exception {
        mockMvc.perform(get(urlBase))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Invalid credentials")
                                .build()),
                        JsonCompareMode.STRICT));
    }
}