package com.hackathon.blockchain.controller.blockchain;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlockchainControllerValidateTest extends BaseControllerTest {
    public final String urlBase = "/blockchain/validate";

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    public void blockchainController_validate_ok() throws Exception {
        User user = userRepository.findByUsername("test").get();
        walletService.buyAsset(user.getId(), "BTC", 0.00001);
        blockchainService.mine();

        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Blockchain valid: true")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    public void blockchainController_validate_invalidChain() throws Exception {
        User user = userRepository.findByUsername("test").get();
        walletService.buyAsset(user.getId(), "BTC", 0.00001);
        blockchainService.mine();
        blockRepository.save(blockRepository.findFirstByOrderByIdDesc().get().toBuilder().hash("0000f393638957ef5d7d835a9fe2e5654da67b9303abbd02ae8e82dc0762f896").build());

        mockMvc.perform(get(urlBase))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Blockchain valid: false")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}