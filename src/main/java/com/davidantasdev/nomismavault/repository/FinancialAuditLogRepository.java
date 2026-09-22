package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.FinancialAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialAuditLogRepository extends JpaRepository<FinancialAuditLog, Long> {}
