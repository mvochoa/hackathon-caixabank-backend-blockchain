package com.hackathon.blockchain.controller.contract;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.CreateSmartContractDto;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.model.WalletKey;
import com.hackathon.blockchain.utils.SignatureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContractControllerCreateTest extends BaseControllerTest {
    public final String urlBase = "/contracts/create";
    CreateSmartContractDto data = CreateSmartContractDto.builder()
            .name("Contract example")
            .conditionExpression("#amount > 10")
            .action("CANCEL_TRANSACTION")
            .actionValue(0.0)
            .build();

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void contractController_create_ok() throws Exception {
        Wallet wallet = walletRepository.findByUserId(userRepository.findByUsername("test").get().getId()).get();
        WalletKey walletKey = generateWalletKey(wallet);
        CreateSmartContractDto newData = data.toBuilder().issuerWalletId(wallet.getId()).build();
        String dataToSign = newData.getName() +
                newData.getConditionExpression() +
                newData.getAction() +
                newData.getActionValue() +
                newData.getIssuerWalletId();

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(newData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.issuerWalletId").value(newData.getIssuerWalletId()))
                .andExpect(jsonPath("$.name").value(newData.getName()))
                .andExpect(jsonPath("$.conditionExpression").value(newData.getConditionExpression()))
                .andExpect(jsonPath("$.action").value(newData.getAction()))
                .andExpect(jsonPath("$.actionValue").value(newData.getActionValue()))
                .andExpect(jsonPath("$.digitalSignature").value(SignatureUtil.signature(dataToSign, walletKeyService.getPrivateKeyForWallet(walletKey.getWallet().getId()))));
    }

}