package com.davidantasdev.AssetAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private BigDecimal price;

    @Column(name = "date", nullable = false)
    @NotNull(message = "A data é obrigatória.")
    private LocalDate date;

    // Construtores
    public PriceHistory() {
    }

    public PriceHistory(Asset asset, BigDecimal price, LocalDate date) {
        this.asset = asset;
        this.price = price;
        this.date = date;
    }

    public PriceHistory(Long id, Asset asset, BigDecimal price, LocalDate date) {
        this.id = id;
        this.asset = asset;
        this.price = price;
        this.date = date;
    }

    // Getters e Setters
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
}

