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

public class WalletControllerBuyTest extends BaseControllerTest {
    public final String urlBase = "/wallet/buy";
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
    public void walletController_buy_ok() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("✅ Asset purchased successfully!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_buy_onlyUSDT() throws Exception {
        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().symbol("USDT").build())))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("✅ USDT purchased successfully!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_buy_whenWalletUserNotExists() throws Exception {
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
    public void walletController_buy_whenLiquidityWalletNotFound() throws Exception {
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
    public void walletController_buy_whenUSDTLiquidityWalletNotFound() throws Exception {
        walletRepository.deleteById(walletRepository.findByAddress("LP-USDT").get().getId());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Liquidity pool for USDT not found!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_buy_whenUSDTLiquidityWalletBalanceEmpty() throws Exception {
        walletRepository.save(userRepository.findByUsername("test").get().getWallet().toBuilder().balance(0.0).build());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data.toBuilder().symbol("USDT").build())))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Insufficient fiat balance to buy USDT!")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_buy_whenAssetUSDTBalanceInsufficient() throws Exception {
        Wallet wallet = userRepository.findByUsername("test").get().getWallet();
        Asset asset = ((List<Asset>) assetRepository.findAll()).stream().filter(x -> x.getWallet().getId().equals(wallet.getId()) && x.getSymbol().equals("USDT")).findFirst().get();
        assetRepository.save(asset.toBuilder().quantity(0.0).build());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Insufficient USDT balance! You must buy USDT first.")
                                .build()),
                        JsonCompareMode.STRICT));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void walletController_buy_whenAssetUSDTNotExists() throws Exception {
        Wallet wallet = userRepository.findByUsername("test").get().getWallet();
        Asset asset = ((List<Asset>) assetRepository.findAll()).stream().filter(x -> x.getWallet().getId().equals(wallet.getId()) && x.getSymbol().equals("USDT")).findFirst().get();
        assetRepository.deleteById(asset.getId());

        mockMvc.perform(post(urlBase)
                        .contentType("application/json")
                        .content(mapper.writeValueAsBytes(data)))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        mapper.writeValueAsString(ResponseMessageDto.builder()
                                .message("❌ Insufficient USDT balance! You must buy USDT first.")
                                .build()),
                        JsonCompareMode.STRICT));
    }

}