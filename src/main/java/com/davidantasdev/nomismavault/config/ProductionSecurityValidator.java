package com.davidantasdev.nomismavault.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Falha rapidamente se produção estiver configurada com segredo JWT inseguro. */
@Component
@Profile("prod")
public class ProductionSecurityValidator {
  @Value("${jwt.secret:}")
  private String jwtSecret;

  @PostConstruct
  void validate() {
    if (jwtSecret.isBlank()
        || "NOMISMAVAULT_SECRET_KEY_CHANGE_ME".equals(jwtSecret)
        || jwtSecret.length() < 32)
      throw new IllegalStateException(
          "JWT_SECRET seguro (mínimo 32 caracteres) é obrigatório em produção");
  }
}
