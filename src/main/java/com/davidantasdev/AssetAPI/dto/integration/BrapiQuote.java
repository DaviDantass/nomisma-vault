package com.davidantasdev.AssetAPI.dto.integration;

import java.math.BigDecimal;

public record BrapiQuote(
    String symbol,
    String longName,
    BigDecimal regularMarketPrice,
    BigDecimal regularMarketChange,
    BigDecimal regularMarketChangePercent
) {
}
