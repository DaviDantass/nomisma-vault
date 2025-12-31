package com.davidantasdev.NomismaVault.dto.response;

import java.math.BigDecimal;

public record AssetInfoDTO(
    String ticker,
    String name,
    BigDecimal price
) {
}
