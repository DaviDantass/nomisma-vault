package com.davidantasdev.AssetAPI.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvestmentResponse(
    Long id,
    Long portfolioId,
    Long assetId,
    BigDecimal quantity,
    BigDecimal averagePrice,
    LocalDate purchaseDate,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
