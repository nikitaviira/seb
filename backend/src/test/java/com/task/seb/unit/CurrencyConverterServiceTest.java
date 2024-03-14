package com.task.seb.unit;

import com.task.seb.domain.rate.CurrencyConverterService;
import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.domain.rate.RateService;
import com.task.seb.domain.rate.RatesSyncService;
import com.task.seb.dto.ConversionRequestDto;
import com.task.seb.dto.ConversionResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.task.seb.util.Currency.*;
import static java.math.BigDecimal.ONE;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterServiceTest {
  @Mock
  private RateService rateService;
  @Mock
  private RatesSyncService ratesSyncService;
  @InjectMocks
  private CurrencyConverterService service;

  @Test
  void shouldThrowExceptionWhenNeitherBaseNorQuoteIsEUR() {
    ConversionRequestDto request = new ConversionRequestDto(USD, GBP, new BigDecimal(100));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> service.convert(request));
    assertEquals("Only conversions from or to EUR are available", exception.getMessage());
  }

  @Test
  void shouldReturnOneAsRateForSameBaseAndQuote() {
    ConversionRequestDto request = new ConversionRequestDto(EUR, EUR, new BigDecimal(100));

    ConversionResultDto result = service.convert(request);
    assertEquals(ONE, result.conversionRate());
    assertEquals(new BigDecimal("100.00"), result.conversionResult());
  }

  @Test
  void shouldReturnRateForConversionFromEUR() {
    when(rateService.getLatestRate(any())).thenReturn(Optional.of(rate("1.169156")));

    ConversionRequestDto request = new ConversionRequestDto(EUR, USD, new BigDecimal(100));
    ConversionResultDto result = service.convert(request);

    assertEquals(new BigDecimal("1.169156"), result.conversionRate());
    assertEquals(new BigDecimal("116.92"), result.conversionResult());
  }

  @Test
  void shouldReturnInvertedRateForConversionToEUR() {
    when(rateService.getLatestRate(any())).thenReturn(Optional.of(rate("1.169156")));

    ConversionRequestDto request = new ConversionRequestDto(USD, EUR, new BigDecimal("15.30"));
    ConversionResultDto result = service.convert(request);

    assertEquals(new BigDecimal("0.855318"), result.conversionRate());
    assertEquals(new BigDecimal("13.09"), result.conversionResult());
  }

  @Test
  void shouldLoadRateWhenNotAvailableInCache() {
    when(rateService.getLatestRate(any())).thenReturn(empty());
    when(ratesSyncService.loadAndSaveHistoricalRates(any())).thenReturn(List.of(rate("1.169156"), rate("1.3512")));

    ConversionRequestDto request = new ConversionRequestDto(EUR, USD, new BigDecimal(100));
    ConversionResultDto result = service.convert(request);

    assertEquals(new BigDecimal("1.3512"), result.conversionRate());
    assertEquals(new BigDecimal("135.12"), result.conversionResult());
  }

  private CurrencyRate rate(String rate) {
    var currencyPairRate = new CurrencyRate();
    currencyPairRate.setRate(new BigDecimal(rate));
    currencyPairRate.setQuote(USD);
    return currencyPairRate;
  }
}
