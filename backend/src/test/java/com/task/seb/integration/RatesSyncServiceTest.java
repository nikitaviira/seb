package com.task.seb.integration;

import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.domain.rate.CurrencyRateRepository;
import com.task.seb.domain.rate.RateService;
import com.task.seb.domain.rate.RatesSyncService;
import com.task.seb.integration.util.IntTestBase;
import com.task.seb.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.task.seb.util.Currency.JPY;
import static com.task.seb.util.Currency.USD;
import static com.task.seb.util.DateUtil.setMockNow;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = "rates-api.base-url=http://localhost:${wiremock.server.port}")
public class RatesSyncServiceTest extends IntTestBase {
  @Autowired
  private RatesSyncService ratesSyncService;
  @Autowired
  private CurrencyRateRepository currencyRateRepository;
  @MockBean
  private RateService rateService;

  @Test
  void loadAndSaveHistoricalRates() {
    Currency quote = USD;
    LocalDate today = LocalDate.of(2024, 3, 14);
    setMockNow(today.atStartOfDay().toInstant(UTC));

    stubFor(get(urlPathEqualTo("/getFxRatesForCurrency"))
        .withQueryParam("ccy", equalTo(quote.toString()))
        .withQueryParam("dtFrom", equalTo("2021-03-14"))
        .withQueryParam("dtTo", equalTo("2024-03-14"))
        .willReturn(aResponse().withBodyFile("historical-rates.xml")));

    List<CurrencyRate> currencyRates = ratesSyncService.loadAndSaveHistoricalRates(quote);
    assertThat(currencyRates).hasSize(16);

    var latest = rate(today, USD, "1.0939");
    latest.setId(1L);
    assertThat(currencyRates.getFirst()).isEqualTo(latest);
  }

  @Test
  void loadAndSaveLatestRates() {
    LocalDate oldDate = LocalDate.of(2024, 3, 10);
    LocalDate today = LocalDate.of(2024, 3, 15);

    when(rateService.getLatestRate(USD)).thenReturn(Optional.of(rate(oldDate, USD, "1.228")));
    when(rateService.getLatestRate(JPY)).thenReturn(Optional.of(rate(today, JPY, "1.23")));

    stubFor(get(urlPathEqualTo("/getCurrentFxRates"))
        .willReturn(aResponse().withBodyFile("latest-rates.xml")));

    ratesSyncService.loadAndSaveLatestRates();

    List<CurrencyRate> rates = currencyRateRepository.findAll();
    assertThat(rates).hasSize(1);

    var usdRate = rate(today, USD, "1.0925");
    usdRate.setId(1L);
    assertThat(rates.getFirst()).isEqualTo(usdRate);
  }

  private CurrencyRate rate(LocalDate date, Currency quote, String rate) {
    var currencyRate = new CurrencyRate();
    currencyRate.setDate(date);
    currencyRate.setQuote(quote);
    currencyRate.setRate(new BigDecimal(rate));
    return currencyRate;
  }
}
