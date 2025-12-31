package com.davidantasdev.nomismavault.integration;

import com.davidantasdev.nomismavault.dto.integration.BrapiQuote;
import com.davidantasdev.nomismavault.dto.integration.BrapiResponse;
import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class BrapiClient {

    private final RestTemplate restTemplate;

    @Value("${brapi.api.url}")
    private String baseUrl;

    public BrapiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "stock-quotes", key = "#ticker")
    public AssetQuoteDTO getQuote(String ticker) {
        BrapiQuote quote = fetchQuote(ticker);
        return new AssetQuoteDTO(
                quote.symbol(),
                quote.regularMarketPrice(),
                quote.regularMarketChange(),
                quote.regularMarketChangePercent(),
                LocalDateTime.now()
        );
    }

    @Cacheable(value = "stock-info", key = "#ticker")
    public AssetInfoDTO getAssetInfo(String ticker) {
        BrapiQuote quote = fetchQuote(ticker);
        return new AssetInfoDTO(
                quote.symbol(),
                quote.longName(),
                quote.regularMarketPrice()
        );
    }

    private BrapiQuote fetchQuote(String ticker) {
        String url = baseUrl + "/quote/" + ticker;
        try {
            BrapiResponse response = restTemplate.getForObject(url, BrapiResponse.class);

            return Optional.ofNullable(response)
                    .map(BrapiResponse::results)
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .orElseThrow(() -> new BusinessException("Quote not found for ticker: " + ticker));

        } catch (Exception e) {
            log.error("Failed to fetch quote for {}: ", ticker, e);
            throw new BusinessException("Failed to fetch quote for ticker: " + ticker);
        }
    }
}
