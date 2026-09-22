package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.marketdata.MarketDataService;
import com.davidantasdev.nomismavault.marketdata.economic.EconomicIndicatorResponse;
import com.davidantasdev.nomismavault.marketdata.economic.EconomicIndicatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
@Tag(name = "Market", description = "Cotações atuais obtidas pela Brapi")
public class MarketController {

  private final MarketDataService marketDataService;
  private final EconomicIndicatorService economicIndicatorService;

  @GetMapping("/quote/{ticker}")
  public ResponseEntity<AssetQuoteDTO> getQuote(@PathVariable String ticker) {
    return ResponseEntity.ok(marketDataService.getQuote(ticker));
  }

  @GetMapping("/info/{ticker}")
  public ResponseEntity<AssetInfoDTO> getInfo(@PathVariable String ticker) {
    return ResponseEntity.ok(marketDataService.getAssetInfo(ticker));
  }

  @GetMapping("/indicators/{indicator}")
  public ResponseEntity<EconomicIndicatorResponse> getEconomicIndicator(
      @PathVariable String indicator) {
    return ResponseEntity.ok(economicIndicatorService.getLatest(indicator));
  }
}
