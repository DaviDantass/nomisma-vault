package com.davidantasdev.nomismavault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Registro imutável de operações financeiras efetuadas pelo usuário. */
@Entity
@Table(name = "financial_audit_logs")
public class FinancialAuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "portfolio_id", nullable = false)
  private Long portfolioId;

  @Column(name = "transaction_id")
  private Long transactionId;

  @Column(nullable = false, length = 30)
  private String action;

  @Column(columnDefinition = "TEXT")
  private String details;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  protected FinancialAuditLog() {}

  public FinancialAuditLog(
      Long userId, Long portfolioId, Long transactionId, String action, String details) {
    this.userId = userId;
    this.portfolioId = portfolioId;
    this.transactionId = transactionId;
    this.action = action;
    this.details = details;
    this.createdAt = LocalDateTime.now();
  }
}
