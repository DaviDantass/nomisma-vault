package com.davidantasdev.nomismavault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "price_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"asset_id", "date"})
})
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @NotNull(message = "O ativo é obrigatório.")
    private Asset asset;

    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
    private BigDecimal price;

    @Column(name = "date", nullable = false)
    @NotNull(message = "A data é obrigatória.")
    private LocalDate date;

    public PriceHistory() {
    }

    public PriceHistory(Asset asset, BigDecimal price, LocalDate date) {
        this.asset = asset;
        this.price = price;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PriceHistory{" +
                "id=" + id +
                ", price=" + price +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceHistory that = (PriceHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(asset, that.asset) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asset, date);
    }
}