package com.task.seb.domain.rate;

import com.task.seb.client.RatesApiClient;
import com.task.seb.client.FxRates;
import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.task.seb.util.DateUtil.today;

@Service
@RequiredArgsConstructor
public class RatesSyncService {
  private final CurrencyRateRepository currencyRateRepository;
  private final RatesApiClient ratesApiClient;
  private final RateService rateService;

  private static final long MAX_HISTORICAL_PERIOD_YEARS = 3L;

  @Transactional
  public List<CurrencyRate> loadAndSaveHistoricalRates(Currency quote) {
    LocalDate to = today();
    LocalDate from = to.minusYears(MAX_HISTORICAL_PERIOD_YEARS);

    List<CurrencyRate> rates = ratesApiClient.getFxRatesForCurrency(quote, from, to).getRates().stream()
        .map(this::toCurrencyPairRate)
        .toList();

    return currencyRateRepository.saveAll(rates);
  }

  @Transactional
  public void loadAndSaveLatestRates() {
    List<CurrencyRate> rates = ratesApiClient.getCurrentFxRates().getRates().stream()
        .map(this::toCurrencyPairRate)
        .filter(rate -> {
          Optional<CurrencyRate> latestRate = rateService.getLatestRate(rate.getQuote());
          return latestRate.isPresent() && rate.getDate().isAfter(latestRate.get().getDate());
        })
        .peek(rate -> rateService.evictRateCache(rate.getQuote()))
        .toList();

    currencyRateRepository.saveAll(rates);
  }

  private CurrencyRate toCurrencyPairRate(FxRates.FxRate fxRate) {
    var rate = new CurrencyRate();
    FxRates.CurrencyAmount quote = fxRate.getCurrencyAmounts().getLast();
    rate.setDate(fxRate.getDate());
    rate.setQuote(quote.getCurrency());
    rate.setRate(quote.getAmount());
    return rate;
  }
}
