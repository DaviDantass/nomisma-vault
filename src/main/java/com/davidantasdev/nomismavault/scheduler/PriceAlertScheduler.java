package com.davidantasdev.nomismavault.scheduler;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import com.davidantasdev.nomismavault.entity.enums.AlertCondition;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceAlertScheduler {

    private final AssetRepository assetRepository;
    private final PriceAlertRepository priceAlertRepository;

    // NotificationService notificationService;
    // send -> mails/push

    // It runs every 5 minutes during market hours.
    @Scheduled(cron = "0 */5 10-18 * * MON-FRI")
    public void checkPriceAlerts() {
        log.info("Checking price alerts...");

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Asset> recentlyUpdatedAssets = assetRepository.findByLastUpdateAfter(threshold);

        for (Asset asset : recentlyUpdatedAssets) {
            List<PriceAlert> alerts = priceAlertRepository.findByAssetAndIsActiveTrue(asset);
            for (PriceAlert alert : alerts) {
                if (shouldTrigger(alert, asset.getCurrentPrice())) {
                    triggerAlert(alert, asset.getCurrentPrice());
                }
            }
        }
        log.info("Price alert check completed");
    }

    private boolean shouldTrigger(PriceAlert alert, BigDecimal currentPrice) {
        if (alert.getCondition() == AlertCondition.ABOVE) {
            return currentPrice.compareTo(alert.getTargetPrice()) >= 0;
        } else {
            return currentPrice.compareTo(alert.getTargetPrice()) <= 0;
        }
    }

    private void triggerAlert(PriceAlert alert, BigDecimal currentPrice) {
        log.info("ALERT TRIGGERED: {} reached {} (target: {} {})",
                alert.getAsset().getTicker(),
                currentPrice,
                alert.getCondition(),
                alert.getTargetPrice());
        alert.setTriggeredAt(LocalDateTime.now());
        alert.setIsActive(false);
        priceAlertRepository.save(alert);
    }
}