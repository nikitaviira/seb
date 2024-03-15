package com.task.seb.domain.rate;

import com.task.seb.dto.ConversionRequestDto;
import com.task.seb.dto.ConversionResultDto;
import com.task.seb.exception.ServiceException;
import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.task.seb.domain.rate.CurrencyRate.BASE_CURRENCY;
import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
  private final RateService rateService;
  private final RatesSyncService ratesSyncService;

  public ConversionResultDto convert(ConversionRequestDto conversionRequest) {
    BigDecimal conversionRate = conversionRate(conversionRequest);
    return new ConversionResultDto(
        conversionRate.stripTrailingZeros(),
        convert(conversionRate, conversionRequest.amount())
    );
  }

  private BigDecimal convert(BigDecimal rate, BigDecimal amount) {
    return amount.multiply(rate).setScale(2, HALF_UP).stripTrailingZeros();
  }

  private BigDecimal conversionRate(ConversionRequestDto conversionRequest) {
    validateCurrency(conversionRequest);
    if (isSameCurrency(conversionRequest)) {
      return ONE;
    }

    Currency conversionCurrency = determineConversionCurrency(conversionRequest);
    BigDecimal rate = fetchConversionRate(conversionCurrency);
    return isEurBaseCurrency(conversionRequest) ? rate : invertRate(rate);
  }

  private void validateCurrency(ConversionRequestDto conversionRequest) {
    if (conversionRequest.base() != BASE_CURRENCY && conversionRequest.quote() != BASE_CURRENCY) {
      throw new ServiceException("Only conversions from or to " + BASE_CURRENCY + " are available");
    }
  }

  private boolean isSameCurrency(ConversionRequestDto conversionRequest) {
    return conversionRequest.base().equals(conversionRequest.quote());
  }

  private Currency determineConversionCurrency(ConversionRequestDto conversionRequest) {
    return isEurBaseCurrency(conversionRequest) ? conversionRequest.quote() : conversionRequest.base();
  }

  private BigDecimal fetchConversionRate(Currency conversionCurrency) {
    return rateService.getLatestRate(conversionCurrency)
        .map(CurrencyRate::getRate)
        .orElseGet(() -> ratesSyncService.loadAndSaveHistoricalRates(conversionCurrency).getFirst().getRate())
        .setScale(6, HALF_UP);
  }

  private boolean isEurBaseCurrency(ConversionRequestDto conversionRequest) {
    return conversionRequest.base() == BASE_CURRENCY;
  }

  private BigDecimal invertRate(BigDecimal rate) {
    return ONE.divide(rate, 6, HALF_UP);
  }
}
