package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
  Page<Investment> findAllByPortfolio(Portfolio portfolio, Pageable pageable);

  List<Investment> findAllByPortfolio(Portfolio portfolio);

  Optional<Investment> findByIdAndPortfolio(Long id, Portfolio portfolio);

  Optional<Investment> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);

  /** Serializa alterações concorrentes na mesma posição durante compra ou venda. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select investment from Investment investment where investment.portfolio = :portfolio and investment.asset = :asset")
  Optional<Investment> findByPortfolioAndAssetForUpdate(Portfolio portfolio, Asset asset);

  @Query("select distinct investment.asset from Investment investment")
  List<Asset> findDistinctAssetsInPortfolios();
}
