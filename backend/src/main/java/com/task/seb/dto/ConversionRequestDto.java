package com.task.seb.dto;

import com.task.seb.util.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequestDto(
    @NotNull(message = "{field.mandatory}")
    Currency base,
    @NotNull(message = "{field.mandatory}")
    Currency quote,
    @DecimalMin(value = "0.0", inclusive = false, message = "{field.moreThanZero}")
    @Digits(integer = 10, fraction = 2, message = "{field.numberOutOfBounds}")
    BigDecimal amount
) {}
