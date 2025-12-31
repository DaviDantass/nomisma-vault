package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findAllByUserId(Long userId);

    Optional<Portfolio> findByIdAndUserId(Long portfolioId, Long userId);
}
