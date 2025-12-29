package com.davidantasdev.AssetAPI.entity;

import com.davidantasdev.AssetAPI.entity.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "investment_categories")
public class InvestmentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Nome da categoria não pode ser vazio")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Nível de risco não pode ser nulo")
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    public InvestmentCategory() {
    }

    public InvestmentCategory(String name, String description, RiskLevel riskLevel) {
        this.name = name;
        this.description = description;
        this.riskLevel = riskLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return "InvestmentCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", riskLevel=" + riskLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InvestmentCategory that = (InvestmentCategory) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && riskLevel == that.riskLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, riskLevel);
    }
}