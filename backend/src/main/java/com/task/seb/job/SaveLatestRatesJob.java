package com.task.seb.job;

import com.task.seb.domain.rate.RatesSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveLatestRatesJob {
  private final RatesSyncService ratesSyncService;

  @Scheduled(cron = "0 0 0-3 * * *")
  public void executeJob() {
    ratesSyncService.loadAndSaveLatestRates();
  }
}
