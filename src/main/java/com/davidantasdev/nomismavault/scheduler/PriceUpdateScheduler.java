package com.davidantasdev.nomismavault.scheduler;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceHistory;
import com.davidantasdev.nomismavault.integration.BrapiClient;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateScheduler {
    private final AssetRepository assetRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final BrapiClient brapiClient;

    // Updates prices every day at 6 PM (after B3 closes)
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void updateAssetPrices() {
        log.info("Starting daily price update...");

        List<Asset> assets = assetRepository.findAll();
        for (Asset asset : assets) {
            try {
                AssetQuoteDTO quote = brapiClient.fetchAssetQuote(asset.getTicker());
                asset.setCurrentPrice(quote.price());
                asset.setLastUpdate(LocalDateTime.now());
                assetRepository.save(asset);

                PriceHistory history = new PriceHistory();
                history.setAsset(asset);
                history.setPrice(quote.price());
                history.setDate(LocalDate.now());
                priceHistoryRepository.save(history);

                log.info("Updated price for {}: {}", asset.getTicker(), quote.price());
            } catch (Exception e) {
                log.error("Failed to update price for {}: {}", asset.getTicker(), e.getMessage());
            }
        }
        log.info("Price update completed");
    }
}
