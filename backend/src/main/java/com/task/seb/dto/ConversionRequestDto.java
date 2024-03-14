package com.task.seb.dto;

import com.task.seb.util.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequestDto(
    @NotNull
    Currency base,
    @NotNull
    Currency quote,
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 3)
    BigDecimal amount
) {}
