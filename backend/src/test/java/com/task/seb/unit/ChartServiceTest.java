package com.task.seb.unit;

import com.task.seb.domain.rate.ChartService;
import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.domain.rate.CurrencyRateRepository;
import com.task.seb.domain.rate.RatesSyncService;
import com.task.seb.dto.ChartDto;
import com.task.seb.dto.ChartPointDto;
import com.task.seb.util.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.task.seb.Helper.rate;
import static com.task.seb.domain.rate.ChartType.*;
import static com.task.seb.domain.rate.CurrencyRate.BASE_CURRENCY;
import static com.task.seb.util.Currency.USD;
import static com.task.seb.util.DateUtil.resetMockNow;
import static com.task.seb.util.DateUtil.setMockNow;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChartServiceTest {
  @Mock
  private CurrencyRateRepository currencyRateRepository;
  @Mock
  private RatesSyncService ratesSyncService;
  @InjectMocks
  private ChartService service;

  @AfterEach
  void afterEach() {
    resetMockNow();
  }

  @Test
  void whenDbRatesEmpty_thenFetchFromService() {
    Currency quote = USD;
    when(currencyRateRepository.findByQuoteAndBaseAndDateGreaterThanEqual(eq(quote), eq(BASE_CURRENCY), any(LocalDate.class)))
        .thenReturn(emptyList());
    when(ratesSyncService.loadAndSaveHistoricalRates(eq(quote))).thenReturn(List.of(rate(LocalDate.now(), quote, "1.2")));

    ChartDto result = service.historicalChart(quote, YEAR);

    assertEquals(1, result.chartPoints().size());
    verify(ratesSyncService).loadAndSaveHistoricalRates(quote);
  }

  @Test
  void whenDbRatesNotEmpty_thenUseDbRates() {
    Currency quote = USD;
    when(currencyRateRepository.findByQuoteAndBaseAndDateGreaterThanEqual(eq(quote), eq(BASE_CURRENCY), any(LocalDate.class)))
        .thenReturn(List.of(rate(LocalDate.now(), quote, "1.2")));

    ChartDto result = service.historicalChart(quote, YEAR);

    assertEquals(1, result.chartPoints().size());
    verify(ratesSyncService, never()).loadAndSaveHistoricalRates(any());
  }

  @Test
  void whenChartTypeIsChanged_thenAmountOfChartPointsIsDifferent() {
    LocalDate today = LocalDate.of(2024, 3, 14);
    setMockNow(today.atStartOfDay().toInstant(UTC));

    Currency quote = USD;
    List<CurrencyRate> dbRates = List.of(
        rate(today, quote, "1.2"),
        rate(today.minusDays(10), quote, "1.4"),
        rate(today.minusMonths(1), quote, "1.5"),
        rate(today.minusMonths(1).minusDays(1), quote, "1.3"),
        rate(today.withDayOfYear(1), quote, "1.7"),
        rate(today.minusYears(1), quote, "1.8"),
        rate(today.minusYears(1).minusDays(1), quote, "1.9")
    );

    when(currencyRateRepository.findByQuoteAndBaseAndDateGreaterThanEqual(eq(quote), eq(BASE_CURRENCY), any(LocalDate.class)))
        .thenReturn(emptyList());
    when(ratesSyncService.loadAndSaveHistoricalRates(eq(quote))).thenReturn(dbRates);

    ChartDto yearResult = service.historicalChart(quote, YEAR);
    ChartDto monthResult = service.historicalChart(quote, MONTH);
    ChartDto ytdResult = service.historicalChart(quote, YTD);
    ChartDto allTimeResult = service.historicalChart(quote, ALL);

    assertEquals(6, yearResult.chartPoints().size());
    assertEquals(3, monthResult.chartPoints().size());
    assertEquals(5, ytdResult.chartPoints().size());
    assertEquals(7, allTimeResult.chartPoints().size());
  }


  @Test
  void whenMultipleRates_thenCalculatePercentageDifferenceCorrectly() {
    Currency quote = USD;
    LocalDate today = LocalDate.of(2024, 3, 14);
    when(currencyRateRepository.findByQuoteAndBaseAndDateGreaterThanEqual(eq(quote), eq(BASE_CURRENCY), any(LocalDate.class)))
        .thenReturn(List.of(
            rate(today.minusDays(1), quote, "1.7"),
            rate(today.minusDays(5), quote, "1.3"),
            rate(today.minusDays(3), quote, "1.5"),
            rate(today, quote, "1.9"))
        );

    ChartDto result = service.historicalChart(quote, MONTH);

    assertEquals(result.changePercent(), new BigDecimal("46.15"));
    assertThat(result.chartPoints()).containsExactly(
        new ChartPointDto(LocalDate.of(2024, 3, 9), new BigDecimal("1.3")),
        new ChartPointDto(LocalDate.of(2024, 3, 11), new BigDecimal("1.5")),
        new ChartPointDto(LocalDate.of(2024, 3, 13), new BigDecimal("1.7")),
        new ChartPointDto(LocalDate.of(2024, 3, 14), new BigDecimal("1.9"))
    );
  }
}
