package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/market")
public class MarketController {

    private final MarketDataService marketDataService;

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> prices() {
        return new ResponseEntity<>(
                marketDataService.fetchLiveMarketPrices(),
                HttpStatus.OK);
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<ResponseMessageDto> price(
            @PathVariable String symbol
    ) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(String.format("Current price of %s: $%f", symbol.toUpperCase(), marketDataService.fetchLivePriceForAsset(symbol.toUpperCase())))
                        .build(),
                HttpStatus.OK);
    }
}