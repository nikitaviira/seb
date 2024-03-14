package com.task.seb.integration;

import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.domain.rate.CurrencyRateRepository;
import com.task.seb.domain.rate.RateService;
import com.task.seb.integration.util.IntTestBase;
import com.task.seb.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.task.seb.util.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RateServiceTest extends IntTestBase {
  @SpyBean
  private CurrencyRateRepository currencyRateRepository;
  @Autowired
  private RateService rateService;

  @Test
  void getLatestRate_firstValueIsCached() {
    Currency quote = USD;
    var rate = new CurrencyRate();
    rate.setQuote(quote);
    rate.setRate(new BigDecimal("1.555"));
    rate.setDate(LocalDate.of(2024, 1, 1));
    currencyRateRepository.save(rate);

    Optional<CurrencyRate> notCachedRate = rateService.getLatestRate(quote);
    assertThat(notCachedRate).isPresent();
    assertThat(notCachedRate.get().getRate()).isEqualTo(new BigDecimal("1.555"));

    Optional<CurrencyRate> cachedRate = rateService.getLatestRate(quote);
    assertThat(cachedRate).isPresent();
    assertThat(cachedRate.get().getRate()).isEqualTo(new BigDecimal("1.555"));

    verify(currencyRateRepository, times(1)).findFirstByQuoteOrderByDateDesc(quote);
  }

  @Test
  void evictRateCache_noCacheIfValueIsEmpty() {
    Currency quote = USD;
    rateService.getLatestRate(quote);
    rateService.getLatestRate(quote);
    verify(currencyRateRepository, times(2)).findFirstByQuoteOrderByDateDesc(quote);
  }

  @Test
  void evictRateCache() {
    Currency quote = USD;
    var rate = new CurrencyRate();
    rate.setQuote(quote);
    rate.setRate(new BigDecimal("1.555"));
    rate.setDate(LocalDate.of(2024, 1, 1));
    currencyRateRepository.save(rate);

    rateService.getLatestRate(quote);
    rateService.evictRateCache(quote);
    rateService.getLatestRate(quote);
    verify(currencyRateRepository, times(2)).findFirstByQuoteOrderByDateDesc(quote);
  }
}
