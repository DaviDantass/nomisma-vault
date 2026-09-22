package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.PortfolioSnapshot;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
  boolean existsByPortfolioAndSnapshotDate(Portfolio portfolio, LocalDate snapshotDate);
}
