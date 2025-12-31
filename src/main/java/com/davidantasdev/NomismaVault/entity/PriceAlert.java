package com.davidantasdev.NomismaVault.entity;

import com.davidantasdev.NomismaVault.entity.enums.AlertCondition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuário não pode ser nulo")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @NotNull(message = "Ativo não pode ser nulo")
    private Asset asset;

    @Column(name = "target_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Preço alvo não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço alvo deve ser maior que 0")
    private BigDecimal targetPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", length = 10)
    private AlertCondition condition;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public PriceAlert() {
    }

    public PriceAlert(User user, Asset asset, BigDecimal targetPrice, AlertCondition condition) {
        this.user = user;
        this.asset = asset;
        this.targetPrice = targetPrice;
        this.condition = condition;
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public AlertCondition getCondition() {
        return condition;
    }

    public void setCondition(AlertCondition condition) {
        this.condition = condition;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PriceAlert{" +
                "id=" + id +
                ", targetPrice=" + targetPrice +
                ", condition=" + condition +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceAlert that = (PriceAlert) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(asset, that.asset) && Objects.equals(targetPrice, that.targetPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, asset, targetPrice);
    }
}
