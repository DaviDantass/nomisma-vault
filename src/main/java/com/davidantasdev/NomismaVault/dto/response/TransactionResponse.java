package com.davidantasdev.NomismaVault.dto.response;

import com.davidantasdev.NomismaVault.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    Long portfolioId,
    Long assetId,
    TransactionType type,
    BigDecimal quantity,
    BigDecimal price,
    BigDecimal totalAmount,
    BigDecimal fees,
    LocalDate transactionDate,
    String notes,
    LocalDateTime createdAt
) {
}
