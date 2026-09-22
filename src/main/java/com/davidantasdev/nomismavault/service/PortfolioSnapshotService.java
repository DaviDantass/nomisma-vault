package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.PortfolioSnapshot;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.PortfolioSnapshotRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Persiste a evolução diária da carteira usando somente valores já conhecidos localmente. */
@Service
@RequiredArgsConstructor
public class PortfolioSnapshotService {
  private final PortfolioRepository portfolioRepository;
  private final PortfolioSnapshotRepository snapshotRepository;
  private final PortfolioService portfolioService;

  @Transactional
  public void captureDailySnapshots() {
    LocalDate today = LocalDate.now();
    for (Portfolio portfolio : portfolioRepository.findAll()) {
      if (snapshotRepository.existsByPortfolioAndSnapshotDate(portfolio, today)) continue;
      PortfolioSummaryResponse summary =
          portfolioService.getSummary(portfolio.getUser().getId(), portfolio.getId());
      snapshotRepository.save(
          new PortfolioSnapshot(
              portfolio,
              summary.totalInvested(),
              summary.currentValue(),
              summary.profitLoss(),
              summary.profitLossPercent(),
              today));
    }
  }
}
