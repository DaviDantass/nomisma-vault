package com.davidantasdev.nomismavault.exception;

/** O provedor não possui cotação para o ativo solicitado. */
public class MarketDataNotFoundException extends RuntimeException {
  public MarketDataNotFoundException(String message) {
    super(message);
  }

  public MarketDataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
