package com.hackathon.blockchain.controller.wallet;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.WalletKeyDto;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.model.WalletKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerGenerateKeysTest extends BaseControllerTest {
    public final String urlBase = "/wallet/generate-keys";

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_generateKeys_ok() throws Exception {
        Wallet wallet = generateWallet();

        ResultActions resultActions = mockMvc.perform(post(urlBase));

        WalletKey walletKey = walletRepository.findById(wallet.getId()).get().getWalletKey();
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(WalletKeyDto.builder()
                                .message(String.format("Keys generated/retrieved successfully for wallet id: %d", wallet.getId()))
                                .publicKey(walletKey.getPublicKey())
                                .absolutePath(Path.of("keys").toAbsolutePath().toString())
                                .build()),
                        JsonCompareMode.STRICT));
    }

}