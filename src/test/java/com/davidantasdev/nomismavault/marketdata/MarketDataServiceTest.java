package com.davidantasdev.nomismavault.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.marketdata.provider.MarketDataProvider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketDataServiceTest {

  @Mock MarketDataProvider marketDataProvider;
  @InjectMocks MarketDataService marketDataService;

  @Test
  void getQuoteNormalizesTickerBeforeCallingProvider() {
    AssetQuoteDTO quote =
        new AssetQuoteDTO(
            "PETR4", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now());
    when(marketDataProvider.getQuote("PETR4")).thenReturn(quote);

    AssetQuoteDTO result = marketDataService.getQuote("petr4");

    assertEquals(quote, result);
    verify(marketDataProvider).getQuote("PETR4");
  }
}
