package com.davidantasdev.NomismaVault.repository;

import com.davidantasdev.NomismaVault.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findAllByUserId(Long userId);

    Optional<Portfolio> findByIdAndUserId(Long portfolioId, Long userId);
}
