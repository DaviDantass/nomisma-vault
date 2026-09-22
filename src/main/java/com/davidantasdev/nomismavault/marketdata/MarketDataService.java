package com.davidantasdev.nomismavault.marketdata;

import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.marketdata.provider.MarketDataProvider;
import java.util.Locale;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MarketDataService {

  private final MarketDataProvider marketDataProvider;

  public MarketDataService(MarketDataProvider marketDataProvider) {
    this.marketDataProvider = marketDataProvider;
  }

  @Cacheable(value = "stock-quotes", key = "#ticker")
  public AssetQuoteDTO getQuote(String ticker) {
    return marketDataProvider.getQuote(normalizeTicker(ticker));
  }

  @Cacheable(value = "stock-info", key = "#ticker")
  public AssetInfoDTO getAssetInfo(String ticker) {
    return marketDataProvider.getAssetInfo(normalizeTicker(ticker));
  }

  private String normalizeTicker(String ticker) {
    return ticker.toUpperCase(Locale.ROOT);
  }
}
