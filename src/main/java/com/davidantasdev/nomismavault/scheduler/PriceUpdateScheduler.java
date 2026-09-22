package com.davidantasdev.nomismavault.scheduler;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceHistory;
import com.davidantasdev.nomismavault.marketdata.MarketDataService;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.PriceHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateScheduler {
  private final AssetRepository assetRepository;
  private final PriceHistoryRepository priceHistoryRepository;
  private final MarketDataService marketDataService;
  private final InvestmentRepository investmentRepository;
  private final PriceAlertRepository priceAlertRepository;

  // Updates prices every day at 6 PM (after B3 closes)
  @Scheduled(cron = "0 0 18 * * MON-FRI")
  public void updateAssetPrices() {
    log.info("Starting daily price update...");

    Set<Asset> assets = new LinkedHashSet<>(investmentRepository.findDistinctAssetsInPortfolios());
    assets.addAll(priceAlertRepository.findDistinctAssetsWithActiveAlerts());
    log.info("Updating prices for {} monitored assets", assets.size());

    for (Asset asset : assets) {
      try {
        AssetQuoteDTO quote = marketDataService.getQuote(asset.getTicker());
        asset.updateLastKnownPrice(quote.price(), LocalDateTime.now());
        assetRepository.save(asset);

        if (!priceHistoryRepository.existsByAssetIdAndDate(asset.getId(), LocalDate.now())) {
          PriceHistory history = new PriceHistory();
          history.setAsset(asset);
          history.setPrice(quote.price());
          history.setDate(LocalDate.now());
          priceHistoryRepository.save(history);
        }

        log.info("Updated price for {}: {}", asset.getTicker(), quote.price());
      } catch (Exception e) {
        log.error("Failed to update price for {}: {}", asset.getTicker(), e.getMessage());
      }
    }
    log.info("Price update completed");
  }
}
