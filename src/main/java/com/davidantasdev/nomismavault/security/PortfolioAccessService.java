package com.davidantasdev.nomismavault.security;

import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioAccessService {

  private final PortfolioRepository portfolioRepository;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  public PortfolioAccessService(
      PortfolioRepository portfolioRepository,
      AuthenticatedUserProvider authenticatedUserProvider) {
    this.portfolioRepository = portfolioRepository;
    this.authenticatedUserProvider = authenticatedUserProvider;
  }

  @Transactional(readOnly = true)
  public Portfolio getOwnedPortfolio(Long portfolioId) {
    Portfolio portfolio =
        portfolioRepository
            .findById(portfolioId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

    authenticatedUserProvider.validateOwnership(portfolio.getUser().getId());
    return portfolio;
  }
}
