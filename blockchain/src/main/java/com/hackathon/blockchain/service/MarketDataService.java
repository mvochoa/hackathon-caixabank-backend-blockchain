package com.hackathon.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@AllArgsConstructor
public class MarketDataService {

    private final ObjectMapper mapper;

    public BigDecimal fetchLivePriceForAsset(String symbol) {
        BigDecimal price = fetchLiveMarketPrices().get(symbol);
        if (price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Current price of %s: $%g", symbol, 0.0));
        }

        return price;
    }

    public Map<String, BigDecimal> fetchLiveMarketPrices() {
        String apiUrl = "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-3d8ede30-848f-4a7a-acc2-22ba0cd9a382/default/fake-market-prices";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                mapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
                return mapper.readValue(response.body(), new TypeReference<>() {
                });
            }
        } catch (IOException | InterruptedException e) {
        } finally {
            httpClient.close();
        }

        return Map.of();
    }
}