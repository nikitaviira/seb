package com.task.seb.dto;

import java.math.BigDecimal;

public record ConversionResultDto(
    CurrencyDto base,
    CurrencyDto quote,
    BigDecimal amount,
    BigDecimal conversionRate,
    BigDecimal invertedConversionRate,
    BigDecimal conversionResult
) {}