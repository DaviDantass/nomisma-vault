package com.davidantasdev.nomismavault.exception;

/** O provedor recusou a consulta por limite de consumo. */
public class MarketDataRateLimitException extends RuntimeException {
  public MarketDataRateLimitException(String message, Throwable cause) {
    super(message, cause);
  }
}
