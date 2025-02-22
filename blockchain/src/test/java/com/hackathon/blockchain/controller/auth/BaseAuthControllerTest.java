package com.hackathon.blockchain.controller.auth;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.model.User;
import org.junit.jupiter.api.BeforeEach;

public class BaseAuthControllerTest extends BaseControllerTest {
    protected final String userPassword = "password";
    protected final User user = User.builder()
            .email("test@example.com")
            .username("test")
            .build();

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        userRepository.save(user.toBuilder().password(passwordEncoder.encode(userPassword)).build());
    }
}