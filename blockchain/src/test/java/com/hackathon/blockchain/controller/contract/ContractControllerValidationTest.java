package com.hackathon.blockchain.controller.contract;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.CreateSmartContractDto;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.SmartContract;
import com.hackathon.blockchain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContractControllerValidationTest extends BaseControllerTest {
    public final String urlBase = "/contracts/validate/{id}";
    CreateSmartContractDto data = CreateSmartContractDto.builder()
            .name("Contract example")
            .conditionExpression("#amount > 10")
            .action("CANCEL_TRANSACTION")
            .actionValue(0.0)
            .build();

    @BeforeEach
    public void setUp() {
        super.setUp();
        Wallet wallet = generateWallet();
        generateWalletKey(wallet);
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void contractController_validate_ok() throws Exception {
        Wallet wallet = walletRepository.findByUserId(userRepository.findByUsername("test").get().getId()).get();
        CreateSmartContractDto newData = data.toBuilder().issuerWalletId(wallet.getId()).build();

        SmartContract smartContract = smartContractEvaluationService.create(newData);
        mockMvc.perform(get(urlBase, smartContract.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Smart contract is valid")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void contractController_validate_invalid() throws Exception {
        Wallet wallet = walletRepository.findByUserId(userRepository.findByUsername("test").get().getId()).get();
        CreateSmartContractDto newData = data.toBuilder().issuerWalletId(wallet.getId()).build();

        SmartContract smartContract = smartContractEvaluationService.create(newData);
        smartContractRepository.save(smartContract.toBuilder().actionValue(5.0).build());
        
        mockMvc.perform(get(urlBase, smartContract.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("Smart contract is invalid")
                                .build()),
                        JsonCompareMode.STRICT));
    }
}