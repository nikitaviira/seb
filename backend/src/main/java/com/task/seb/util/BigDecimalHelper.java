package com.task.seb.util;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

public class BigDecimalHelper {
  public static final int PRESENTATION_SCALE = 6;
  public static final int CURRENCY_SCALE = 2;
  public static final int PERCENT_SCALE = 2;
  public static final int CALCULATION_SCALE = 10;

  public static BigDecimal calculatePercentageDifference(BigDecimal v1, BigDecimal v2) {
    return v2.subtract(v1)
        .divide(v1, CALCULATION_SCALE, HALF_UP)
        .multiply(new BigDecimal(100))
        .setScale(PERCENT_SCALE, HALF_UP);
  }
}
