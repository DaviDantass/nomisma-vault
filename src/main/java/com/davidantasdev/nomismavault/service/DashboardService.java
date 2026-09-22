package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.DashboardResponse;
import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final UserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final PriceAlertRepository priceAlertRepository;
  private final PortfolioService portfolioService;

  @Transactional(readOnly = true)
  public DashboardResponse getForUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    List<PortfolioSummaryResponse> portfolios =
        portfolioRepository.findAllByUser(user).stream()
            .map(p -> portfolioService.getSummary(userId, p.getId()))
            .toList();
    BigDecimal invested =
        portfolios.stream()
            .map(PortfolioSummaryResponse::totalInvested)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal value =
        portfolios.stream()
            .map(PortfolioSummaryResponse::currentValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal pnl = value.subtract(invested);
    BigDecimal percent =
        invested.signum() == 0
            ? BigDecimal.ZERO
            : pnl.divide(invested, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    return new DashboardResponse(
        portfolios.size(),
        (int) priceAlertRepository.countByUserAndIsActive(user, true),
        invested,
        value,
        pnl,
        percent,
        portfolios);
  }
}
