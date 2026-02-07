package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByPortfolio(Portfolio portfolio, Pageable pageable);

    Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);

    Page<Transaction> findByPortfolioIdAndTransactionDateBetween(
            Long portfolioId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
