package com.task.seb;

import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.util.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Helper {
  public static CurrencyRate rate(LocalDate date, Currency quote, String rate) {
    var currencyRate = new CurrencyRate();
    currencyRate.setDate(date);
    currencyRate.setQuote(quote);
    currencyRate.setRate(new BigDecimal(rate));
    return currencyRate;
  }
}
