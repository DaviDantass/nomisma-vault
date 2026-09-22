package com.davidantasdev.nomismavault.exception;

/** O provedor de dados de mercado não respondeu dentro do contrato esperado. */
public class MarketDataUnavailableException extends RuntimeException {
  public MarketDataUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
