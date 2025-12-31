package com.davidantasdev.NomismaVault.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetQuoteDTO(
    String ticker,
    BigDecimal price,
    BigDecimal change,
    BigDecimal changePercent,
    LocalDateTime timestamp
) {
}
