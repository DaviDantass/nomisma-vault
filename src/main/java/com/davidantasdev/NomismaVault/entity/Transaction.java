package com.davidantasdev.NomismaVault.entity;

import com.davidantasdev.NomismaVault.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    @NotNull(message = "Tipo de transação não pode ser nulo")
    private TransactionType type;

    @Column(name = "quantity", precision = 20, scale = 8, nullable = false)
    @NotNull(message = "Quantidade não pode ser nula")
    @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que 0")
    private BigDecimal quantity;

    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Preço não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
    private BigDecimal price;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Valor total não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que 0")
    private BigDecimal totalAmount;

    @Column(name = "fees", precision = 10, scale = 2)
    @DecimalMin(value = "0", message = "Taxa não pode ser negativa")
    private BigDecimal fees = BigDecimal.ZERO;

    @Column(name = "transaction_date", nullable = false)
    @NotNull(message = "Data da transação não pode ser nula")
    private LocalDate transactionDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Portfolio portfolio, Asset asset, TransactionType type, BigDecimal quantity,
                       BigDecimal price, BigDecimal totalAmount, LocalDate transactionDate) {
        this.portfolio = portfolio;
        this.asset = asset;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.transactionDate = transactionDate;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalAmount=" + totalAmount +
                ", transactionDate=" + transactionDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(portfolio, that.portfolio) && Objects.equals(asset, that.asset) && type == that.type && Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, portfolio, asset, type, transactionDate);
    }
}
