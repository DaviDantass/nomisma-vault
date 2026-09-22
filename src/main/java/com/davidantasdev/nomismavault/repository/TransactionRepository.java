package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  Page<Transaction> findAllByPortfolio(Portfolio portfolio, Pageable pageable);

  Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);

  List<Transaction> findAllByPortfolioAndAssetOrderByTransactionDateAscIdAsc(
      Portfolio portfolio, Asset asset);

  Page<Transaction> findByPortfolioIdAndTransactionDateBetween(
      Long portfolioId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
