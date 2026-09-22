package com.davidantasdev.nomismavault.marketdata.provider;

import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.integration.BrapiClient;
import org.springframework.stereotype.Component;

@Component
public class BrapiMarketDataProvider implements MarketDataProvider {

  private final BrapiClient brapiClient;

  public BrapiMarketDataProvider(BrapiClient brapiClient) {
    this.brapiClient = brapiClient;
  }

  @Override
  public AssetQuoteDTO getQuote(String ticker) {
    return brapiClient.fetchAssetQuote(ticker);
  }

  @Override
  public AssetInfoDTO getAssetInfo(String ticker) {
    return brapiClient.fetchAssetInfo(ticker);
  }
}
