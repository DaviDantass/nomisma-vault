package com.davidantasdev.nomismavault.dto.response;

import com.davidantasdev.nomismavault.entity.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetResponse(
    Long id,
    String ticker,
    String name,
    String categoryName,
    RiskLevel riskLevel,
    BigDecimal currentPrice,
    LocalDateTime lastUpdate,
    LocalDateTime createdAt
) {
}
