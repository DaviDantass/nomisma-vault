package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentCategoryRepository extends JpaRepository<InvestmentCategory, Long> {

  Optional<InvestmentCategory> findByName(String name);

  boolean existsByName(String name);
}
