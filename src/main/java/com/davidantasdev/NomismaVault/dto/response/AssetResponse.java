package com.davidantasdev.NomismaVault.dto.response;

import com.davidantasdev.NomismaVault.entity.enums.RiskLevel;

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
