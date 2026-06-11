package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    Page<Investment> findAllByPortfolio(Portfolio portfolio, Pageable pageable);

    List<Investment> findAllByPortfolio(Portfolio portfolio);

    Optional<Investment> findByIdAndPortfolio(Long id, Portfolio portfolio);

    Optional<Investment> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);
}
