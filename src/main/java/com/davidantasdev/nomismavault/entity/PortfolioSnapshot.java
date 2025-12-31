package com.davidantasdev.nomismavault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
    @NotNull(message = "Portfólio não pode ser nulo")
    private Portfolio portfolio;

    @Column(name = "total_invested", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Total investido não pode ser nulo")
    @DecimalMin(value = "0", message = "Total investido não pode ser negativo")
    private BigDecimal totalInvested;

    @Column(name = "current_value", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "O valor atual é obrigatório.")
    @DecimalMin(value = "0", message = "Valor atual não pode ser negativo")
    private BigDecimal currentValue;

    @Column(name = "profit_loss", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Lucro/Prejuízo não pode ser nulo")
    private BigDecimal profitLoss;

    @Column(name = "profit_loss_percent", precision = 10, scale = 4, nullable = false)
    @NotNull(message = "Percentual de lucro/prejuízo não pode ser nulo")
    private BigDecimal profitLossPercent;

    @Column(name = "snapshot_date", nullable = false)
    @NotNull(message = "Data do snapshot não pode ser nula")
    private LocalDate snapshotDate;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

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

    @Override
    public String toString() {
        return "PortfolioSnapshot{" +
                "id=" + id +
                ", totalInvested=" + totalInvested +
                ", currentValue=" + currentValue +
                ", profitLoss=" + profitLoss +
                ", snapshotDate=" + snapshotDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioSnapshot that = (PortfolioSnapshot) o;
        return Objects.equals(id, that.id) && Objects.equals(portfolio, that.portfolio) && Objects.equals(snapshotDate, that.snapshotDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, portfolio, snapshotDate);
    }
}

