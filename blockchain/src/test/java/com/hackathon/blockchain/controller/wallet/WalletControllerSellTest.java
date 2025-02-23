package com.hackathon.blockchain.controller.wallet;

import com.hackathon.blockchain.controller.BaseControllerTest;
import com.hackathon.blockchain.dto.BuyAndSellAssetWalletDto;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.Asset;
import com.hackathon.blockchain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.json.JsonCompareMode;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerSellTest extends BaseControllerTest {
    public final String urlBase = "/wallet/sell";
    BuyAndSellAssetWalletDto data = BuyAndSellAssetWalletDto.builder()
            .symbol("BTC")
            .quantity(0.00001)
            .build();

    @BeforeEach
    public void setUp() {
        super.setUp();
        generateWallet();
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_ok() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("✅ Asset sold successfully!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_onlyUSDT() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().symbol("USDT").build())))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("✅ Asset sold successfully!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_whenWalletUserNotExists() throws Exception {
        walletRepository.deleteAll();

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Wallet not found!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_whenLiquidityWalletNotFound() throws Exception {
        walletRepository.deleteById(walletRepository.findByAddress(String.format("LP-%s", data.getSymbol())).get().getId());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message(String.format("❌ Liquidity pool for %s not found!", data.getSymbol()))
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_whenUSDTLiquidityBalanceEmptyAndSellUSDT() throws Exception {
        Wallet wallet = walletRepository.findByAddress("LP-USDT").get();
        Asset asset = ((List<Asset>) assetRepository.findAll()).stream().filter(x -> x.getWallet().getId().equals(wallet.getId()) && x.getSymbol().equals("USDT")).findFirst().get();
        assetRepository.save(asset.toBuilder().quantity(0.0).build());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().symbol("USDT").build())))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Not enough USDT liquidity!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_whenUSDTLiquidityNotFound() throws Exception {
        Wallet wallet = walletRepository.findByAddress("LP-USDT").get();
        walletRepository.deleteById(wallet.getId());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ USDT liquidity pool not found!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_sell_whenUSDTLiquidityBalanceEmpty() throws Exception {
        Wallet wallet = walletRepository.findByAddress("LP-USDT").get();
        Asset asset = ((List<Asset>) assetRepository.findAll()).stream().filter(x -> x.getWallet().getId().equals(wallet.getId()) && x.getSymbol().equals("USDT")).findFirst().get();
        assetRepository.save(asset.toBuilder().quantity(0.0).build());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Not enough USDT in liquidity pool!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}