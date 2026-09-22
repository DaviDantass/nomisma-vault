package com.davidantasdev.nomismavault.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.marketdata.MarketDataService;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.PriceHistoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceUpdateSchedulerTest {

  @Mock AssetRepository assetRepository;
  @Mock PriceHistoryRepository priceHistoryRepository;
  @Mock MarketDataService marketDataService;
  @Mock InvestmentRepository investmentRepository;
  @Mock PriceAlertRepository priceAlertRepository;
  @InjectMocks PriceUpdateScheduler priceUpdateScheduler;

  @Test
  void updatesOnlyAssetsUsedByPortfoliosOrActiveAlertsAndAvoidsDuplicateDailyHistory() {
    Asset portfolioAsset = asset(1L, "PETR4");
    Asset alertAsset = asset(2L, "VALE3");
    when(investmentRepository.findDistinctAssetsInPortfolios()).thenReturn(List.of(portfolioAsset));
    when(priceAlertRepository.findDistinctAssetsWithActiveAlerts())
        .thenReturn(List.of(portfolioAsset, alertAsset));
    when(marketDataService.getQuote("PETR4")).thenReturn(quote("PETR4", "38.50"));
    when(marketDataService.getQuote("VALE3")).thenReturn(quote("VALE3", "62.10"));
    when(priceHistoryRepository.existsByAssetIdAndDate(1L, LocalDate.now())).thenReturn(false);
    when(priceHistoryRepository.existsByAssetIdAndDate(2L, LocalDate.now())).thenReturn(true);

    priceUpdateScheduler.updateAssetPrices();

    assertEquals(new BigDecimal("38.50"), portfolioAsset.getCurrentPrice());
    assertNotNull(portfolioAsset.getLastUpdate());
    assertEquals(new BigDecimal("62.10"), alertAsset.getCurrentPrice());
    verify(marketDataService).getQuote("PETR4");
    verify(marketDataService).getQuote("VALE3");
    verify(assetRepository).save(portfolioAsset);
    verify(assetRepository).save(alertAsset);
    verify(priceHistoryRepository).save(any());
    verify(priceHistoryRepository, never())
        .save(
            org.mockito.ArgumentMatchers.argThat(history -> history.getAsset().equals(alertAsset)));
  }

  private Asset asset(Long id, String ticker) {
    Asset asset = new Asset();
    asset.setId(id);
    asset.setTicker(ticker);
    return asset;
  }

  private AssetQuoteDTO quote(String ticker, String price) {
    return new AssetQuoteDTO(
        ticker, new BigDecimal(price), BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now());
  }
}
