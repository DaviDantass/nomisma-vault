package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentCategoryRepository extends JpaRepository<InvestmentCategory, Long> {
    
    Optional<InvestmentCategory> findByName(String name);
    
    boolean existsByName(String name);
}
