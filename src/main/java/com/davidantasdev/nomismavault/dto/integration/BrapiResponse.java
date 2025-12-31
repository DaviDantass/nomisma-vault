package com.davidantasdev.nomismavault.dto.integration;

import java.util.List;

public record BrapiResponse(
    List<BrapiQuote> results
) {
}
