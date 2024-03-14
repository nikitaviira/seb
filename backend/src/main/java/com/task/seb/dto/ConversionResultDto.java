package com.task.seb.dto;

import java.math.BigDecimal;

public record ConversionResultDto(
    BigDecimal conversionRate,
    BigDecimal conversionResult
) {}