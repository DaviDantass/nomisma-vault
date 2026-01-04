package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findAllByPortfolio(Portfolio portfolio);
    Optional<Investment> findByIdAndPortfolio(Long id, Portfolio portfolio);

}
