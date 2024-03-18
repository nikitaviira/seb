package com.task.seb.unit;

import com.task.seb.domain.rate.CurrencyConverterService;
import com.task.seb.domain.rate.RateService;
import com.task.seb.domain.rate.RatesSyncService;
import com.task.seb.dto.ConversionRequestDto;
import com.task.seb.dto.ConversionResultDto;
import com.task.seb.dto.CurrencyDto;
import com.task.seb.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.task.seb.Helper.rate;
import static com.task.seb.util.Currency.*;
import static com.task.seb.util.DateUtil.today;
import static java.math.BigDecimal.ONE;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
  void whenSmallConversionResult_thenRoundTo6DigitsAfterComma() {
    when(rateService.getLatestRate(any())).thenReturn(Optional.of(rate(today(), JPY, "162.03")));

    ConversionRequestDto request = new ConversionRequestDto(JPY, EUR, new BigDecimal(1));
    ConversionResultDto result = service.convert(request);

    assertThat(result.conversionRate()).isEqualByComparingTo(new BigDecimal("0.006172"));
    assertThat(result.conversionResult()).isEqualByComparingTo(new BigDecimal("0.006172"));
    assertThat(result.invertedConversionRate()).isEqualByComparingTo(new BigDecimal("162.03"));
    assertThat(result.base()).isEqualTo(new CurrencyDto(JPY, "Japanese Yen"));
    assertThat(result.quote()).isEqualTo(new CurrencyDto(EUR, "Euro"));
  }

  @Test
  void shouldThrowExceptionWhenEitherBaseOrQuoteIsUnknown() {
    assertThatThrownBy(() -> service.convert(new ConversionRequestDto(EUR, UNKNOWN, new BigDecimal(100))))
        .isInstanceOf(ServiceException.class)
        .hasMessage("Unsupported currency");

    assertThatThrownBy(() -> service.convert(new ConversionRequestDto(UNKNOWN, EUR, new BigDecimal(100))))
        .isInstanceOf(ServiceException.class)
        .hasMessage("Unsupported currency");
  }

  @Test
  void shouldThrowExceptionWhenNeitherBaseNorQuoteIsEUR() {
    assertThatThrownBy(() -> service.convert(new ConversionRequestDto(USD, GBP, new BigDecimal(100))))
        .isInstanceOf(ServiceException.class)
        .hasMessage("Only conversions from or to EUR are available");
  }

  @Test
  void shouldReturnOneAsRateForSameBaseAndQuote() {
    BigDecimal amount = new BigDecimal(100);
    ConversionRequestDto request = new ConversionRequestDto(EUR, EUR, amount);

    ConversionResultDto result = service.convert(request);
    assertThat(result.conversionRate()).isEqualByComparingTo(ONE);
    assertThat(result.conversionResult()).isEqualByComparingTo(amount);
    assertThat(result.invertedConversionRate()).isEqualByComparingTo(ONE);
    assertThat(result.base()).isEqualTo(new CurrencyDto(EUR, "Euro"));
    assertThat(result.quote()).isEqualTo(new CurrencyDto(EUR, "Euro"));
  }

  @Test
  void shouldReturnRateForConversionFromEUR() {
    when(rateService.getLatestRate(any())).thenReturn(Optional.of(rate(today(), USD, "1.169156")));

    ConversionRequestDto request = new ConversionRequestDto(EUR, USD, new BigDecimal(100));
    ConversionResultDto result = service.convert(request);

    assertThat(result.conversionRate()).isEqualByComparingTo(new BigDecimal("1.169156"));
    assertThat(result.conversionResult()).isEqualByComparingTo(new BigDecimal("116.92"));
    assertThat(result.invertedConversionRate()).isEqualByComparingTo(new BigDecimal("0.855318"));
    assertThat(result.base()).isEqualTo(new CurrencyDto(EUR, "Euro"));
    assertThat(result.quote()).isEqualTo(new CurrencyDto(USD, "US Dollar"));
  }

  @Test
  void shouldReturnInvertedRateForConversionToEUR() {
    when(rateService.getLatestRate(any())).thenReturn(Optional.of(rate(today(), USD, "1.169156")));

    ConversionRequestDto request = new ConversionRequestDto(USD, EUR, new BigDecimal("15.30"));
    ConversionResultDto result = service.convert(request);

    assertThat(result.conversionRate()).isEqualByComparingTo(new BigDecimal("0.855318"));
    assertThat(result.conversionResult()).isEqualByComparingTo(new BigDecimal("13.09"));
    assertThat(result.invertedConversionRate()).isEqualByComparingTo(new BigDecimal("1.169156"));
    assertThat(result.base()).isEqualTo(new CurrencyDto(USD, "US Dollar"));
    assertThat(result.quote()).isEqualTo(new CurrencyDto(EUR, "Euro"));
  }

  @Test
  void shouldLoadRateWhenNotAvailableInCache() {
    when(rateService.getLatestRate(any())).thenReturn(empty());
    when(ratesSyncService.loadAndSaveHistoricalRates(any())).thenReturn(List.of(
        rate(today(), USD, "1.3512"),
        rate(today(), USD, "1.169156")
    ));

    ConversionRequestDto request = new ConversionRequestDto(EUR, USD, new BigDecimal(100));
    ConversionResultDto result = service.convert(request);

    assertThat(result.conversionRate()).isEqualByComparingTo(new BigDecimal("1.3512"));
    assertThat(result.conversionResult()).isEqualByComparingTo(new BigDecimal("135.12"));
    assertThat(result.invertedConversionRate()).isEqualByComparingTo(new BigDecimal("0.740083"));
    assertThat(result.base()).isEqualTo(new CurrencyDto(EUR, "Euro"));
    assertThat(result.quote()).isEqualTo(new CurrencyDto(USD, "US Dollar"));
  }
}
