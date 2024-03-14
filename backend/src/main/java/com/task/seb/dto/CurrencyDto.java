package com.task.seb.dto;

import com.task.seb.util.Currency;

public record CurrencyDto(
  Currency code,
  String fullName
) {}
