package com.davidantasdev.NomismaVault.dto.integration;

import java.util.List;

public record BrapiResponse(
    List<BrapiQuote> results
) {
}
