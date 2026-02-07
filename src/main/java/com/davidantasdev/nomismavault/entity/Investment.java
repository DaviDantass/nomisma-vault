package com.davidantasdev.nomismavault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.davidantasdev.nomismavault.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "investments", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "portfolio_id", "asset_id" })
})
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @NotNull(message = "Portfólio não pode ser nulo")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @NotNull(message = "Ativo não pode ser nulo")
    private Asset asset;

    @Column(name = "quantity", precision = 20, scale = 8, nullable = false)
    @NotNull(message = "Quantidade não pode ser nula")
    @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que 0")
    private BigDecimal quantity;

    @Column(name = "average_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Preço médio não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
    private BigDecimal averagePrice;

    @Column(name = "purchase_date", nullable = false)
    @NotNull(message = "Data de compra não pode ser nula")
    private LocalDate purchaseDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Investment() {
    }

    public Investment(Portfolio portfolio, Asset asset, BigDecimal quantity, BigDecimal averagePrice,
            LocalDate purchaseDate) {
        this.portfolio = portfolio;
        this.asset = asset;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.purchaseDate = purchaseDate;
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

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updatePosition(TransactionType type, BigDecimal transactionQuantity, BigDecimal transactionPrice) {
        if (type == TransactionType.BUY) {
            BigDecimal currentTotal = this.quantity.multiply(this.averagePrice);
            BigDecimal newTotal = transactionQuantity.multiply(transactionPrice);
            BigDecimal newQuantity = this.quantity.add(transactionQuantity);

            this.averagePrice = currentTotal.add(newTotal)
                    .divide(newQuantity, 2, RoundingMode.HALF_UP);
            this.quantity = newQuantity;
        } else if (type == TransactionType.SELL) {
            this.quantity = this.quantity.subtract(transactionQuantity);
        }
    }

    public boolean hasEnoughQuantity(BigDecimal sellQuantity) {
        return this.quantity.compareTo(sellQuantity) >= 0;
    }

    public BigDecimal calculateProfitLoss(BigDecimal currentPrice) {
        return currentPrice.subtract(this.averagePrice).multiply(this.quantity);
    }

    public BigDecimal calculateProfitLossPercent(BigDecimal currentPrice) {
        if (this.averagePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(this.averagePrice)
                .divide(this.averagePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public BigDecimal calculateMarketValue(BigDecimal currentPrice) {
        return currentPrice.multiply(this.quantity);
    }

    public BigDecimal calculateTotalInvested() {
        return this.averagePrice.multiply(this.quantity);
    }
    @Override
    public String toString() {
        return "Investment{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", averagePrice=" + averagePrice +
                ", purchaseDate=" + purchaseDate +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Investment that = (Investment) o;
        return Objects.equals(id, that.id) && Objects.equals(portfolio, that.portfolio)
                && Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, portfolio, asset);
    }
}
