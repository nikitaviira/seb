package com.task.seb.domain.rate;

import com.task.seb.dto.ChartDto;
import com.task.seb.dto.ChartPointDto;
import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.task.seb.domain.rate.CurrencyRate.BASE_CURRENCY;
import static com.task.seb.util.Currency.UNKNOWN;
import static com.task.seb.util.DateUtil.today;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.time.LocalDate.EPOCH;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class ChartService {
  private final CurrencyRateRepository currencyRateRepository;
  private final RatesSyncService ratesSyncService;

  @Transactional
  public ChartDto historicalChart(Currency quote, ChartType chartType) {
    if (quote == UNKNOWN || quote == BASE_CURRENCY) {
      return emptyChart();
    }

    LocalDate today = today();
    LocalDate startDate = chartStartDate(chartType, today);
    List<CurrencyRate> dbRates = fetchRates(quote, startDate);

    List<ChartPointDto> chartPoints = convertToChartPoints(dbRates);
    if (chartPoints.isEmpty()) {
      return emptyChart();
    }

    BigDecimal percentageDifference = calculatePercentageDifference(chartPoints);
    return new ChartDto(chartPoints, percentageDifference);
  }

  private List<CurrencyRate> fetchRates(Currency quote, LocalDate startDate) {
    List<CurrencyRate> dbRates = currencyRateRepository.findByQuoteAndDateGreaterThanEqual(quote, startDate);

    if (!dbRates.isEmpty()) {
      return dbRates;
    }

    if (currencyRateRepository.countByQuote(quote) > 0) {
      return emptyList();
    }

    return ratesSyncService.loadAndSaveHistoricalRates(quote).stream()
        .filter(rate -> !rate.getDate().isBefore(startDate))
        .toList();
  }

  private List<ChartPointDto> convertToChartPoints(List<CurrencyRate> rates) {
    return rates.stream()
        .sorted(comparing(CurrencyRate::getDate))
        .map(rate -> new ChartPointDto(rate.getDate(), rate.getRate()))
        .toList();
  }

  private BigDecimal calculatePercentageDifference(List<ChartPointDto> chartPoints) {
    if (chartPoints.size() < 2) {
      return ZERO;
    }

    BigDecimal startValue = chartPoints.getFirst().value();
    BigDecimal endValue = chartPoints.getLast().value();

    return startValue.compareTo(ZERO) == 0 ? ZERO : calculatePercentageDifference(startValue, endValue);
  }

  private BigDecimal calculatePercentageDifference(BigDecimal startValue, BigDecimal endValue) {
    return endValue.subtract(startValue)
        .divide(startValue, 6, HALF_UP)
        .multiply(new BigDecimal(100))
        .setScale(2, HALF_UP);
  }

  private ChartDto emptyChart() {
    return new ChartDto(emptyList(), ZERO);
  }

  private LocalDate chartStartDate(ChartType chartType, LocalDate today) {
    return switch (chartType) {
      case ALL -> EPOCH;
      case YEAR -> today.minusYears(1);
      case YTD -> today.withDayOfYear(1);
      case MONTH -> today.minusMonths(1);
    };
  }
}
