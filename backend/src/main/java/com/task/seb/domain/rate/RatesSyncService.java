package com.task.seb.domain.rate;

import com.task.seb.client.ExchangeRateType;
import com.task.seb.client.FxRates;
import com.task.seb.client.FxRates.FxRate;
import com.task.seb.client.RatesApiClient;
import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.task.seb.client.ExchangeRateType.LT;
import static com.task.seb.util.Currency.UNKNOWN;
import static com.task.seb.util.DateUtil.today;

@Service
@RequiredArgsConstructor
public class RatesSyncService {
  private final CurrencyRateRepository currencyRateRepository;
  private final RatesApiClient ratesApiClient;
  private final RateService rateService;

  private static final long MAX_HISTORICAL_PERIOD_YEARS = 3L;
  private static final ExchangeRateType DEFAULT_EXCHANGE_RATE_TYPE = LT;

  @Transactional
  public List<CurrencyRate> loadAndSaveHistoricalRates(Currency quote) {
    LocalDate to = today();
    LocalDate from = to.minusYears(MAX_HISTORICAL_PERIOD_YEARS);

    List<CurrencyRate> rates = fetchRates(() ->
        ratesApiClient.getFxRatesForCurrency(DEFAULT_EXCHANGE_RATE_TYPE, quote, from, to)).toList();
    return currencyRateRepository.saveAll(rates);
  }

  @Transactional
  public void loadAndSaveLatestRates() {
    List<CurrencyRate> rates = fetchRates(() -> ratesApiClient.getCurrentFxRates(DEFAULT_EXCHANGE_RATE_TYPE))
        .filter(ignoreUnknownCurrencies().and(isNewRate()))
        .peek(rate -> rateService.evictRateCache(rate.getQuote()))
        .toList();
    currencyRateRepository.saveAll(rates);
  }

  private Stream<CurrencyRate> fetchRates(Supplier<FxRates> fetcher) {
    return fetcher.get().getRates().stream().map(this::toCurrencyPairRate);
  }

  private Predicate<CurrencyRate> ignoreUnknownCurrencies() {
    return rate -> rate.getQuote() != UNKNOWN;
  }

  private Predicate<CurrencyRate> isNewRate() {
    return rate -> rateService.getLatestRate(rate.getQuote())
        .map(latestRate -> rate.getDate().isAfter(latestRate.getDate()))
        .orElse(false);
  }

  private CurrencyRate toCurrencyPairRate(FxRate fxRate) {
    var rate = new CurrencyRate();
    FxRates.CurrencyAmount quote = fxRate.getCurrencyAmounts().getLast();
    rate.setDate(fxRate.getDate());
    rate.setQuote(quote.getCurrency());
    rate.setRate(quote.getAmount());
    return rate;
  }
}
