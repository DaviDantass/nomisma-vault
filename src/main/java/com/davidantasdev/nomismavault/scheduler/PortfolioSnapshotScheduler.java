package com.davidantasdev.nomismavault.scheduler;

import com.davidantasdev.nomismavault.service.PortfolioSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioSnapshotScheduler {
  private final PortfolioSnapshotService portfolioSnapshotService;

  @Scheduled(cron = "0 5 18 * * MON-FRI")
  public void captureDailySnapshots() {
    log.info("Capturing daily portfolio snapshots");
    portfolioSnapshotService.captureDailySnapshots();
  }
}
