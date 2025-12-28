package com.davidantasdev.AssetAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_snapshots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"portfolio_id", "snapshot_date"})
})
public class PortfolioSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "total_invested", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalInvested;

    @Column(name = "current_value", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "O valor atual é obrigatório.")
    private BigDecimal currentValue;

    @Column(name = "profit_loss", precision = 15, scale = 2, nullable = false)
    private BigDecimal profitLoss;

    @Column(name = "profit_loss_percent", precision = 10, scale = 4, nullable = false)
    private BigDecimal profitLossPercent;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Construtores
    public PortfolioSnapshot() {
    }

    public PortfolioSnapshot(Portfolio portfolio, BigDecimal totalInvested, BigDecimal currentValue,
                             BigDecimal profitLoss, BigDecimal profitLossPercent, LocalDate snapshotDate) {
        this.portfolio = portfolio;
        this.totalInvested = totalInvested;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercent = profitLossPercent;
        this.snapshotDate = snapshotDate;
    }

    public PortfolioSnapshot(Long id, Portfolio portfolio, BigDecimal totalInvested, BigDecimal currentValue,
                             BigDecimal profitLoss, BigDecimal profitLossPercent, LocalDate snapshotDate, LocalDateTime createdAt) {
        this.id = id;
        this.portfolio = portfolio;
        this.totalInvested = totalInvested;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercent = profitLossPercent;
        this.snapshotDate = snapshotDate;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public BigDecimal getProfitLossPercent() {
        return profitLossPercent;
    }

    public void setProfitLossPercent(BigDecimal profitLossPercent) {
        this.profitLossPercent = profitLossPercent;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
