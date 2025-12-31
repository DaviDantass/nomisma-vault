package com.davidantasdev.NomismaVault.dto.response;

import java.time.LocalDateTime;

public record PortfolioResponse(
    Long id,
    String name,
    String description,
    Long userId,
    LocalDateTime createdAt
) {
}