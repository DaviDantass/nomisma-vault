package com.davidantasdev.nomismavault.marketdata.provider;

import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;

public interface MarketDataProvider {

  AssetQuoteDTO getQuote(String ticker);

  AssetInfoDTO getAssetInfo(String ticker);
}
