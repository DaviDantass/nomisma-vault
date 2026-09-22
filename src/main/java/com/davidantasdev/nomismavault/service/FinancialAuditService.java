package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.entity.FinancialAuditLog;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import com.davidantasdev.nomismavault.repository.FinancialAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialAuditService {
  private final FinancialAuditLogRepository repository;

  public void record(String action, Portfolio portfolio, Transaction transaction) {
    String details =
        transaction.getType()
            + " "
            + transaction.getQuantity()
            + " "
            + transaction.getAsset().getTicker();
    repository.save(
        new FinancialAuditLog(
            portfolio.getUser().getId(), portfolio.getId(), transaction.getId(), action, details));
  }
}
