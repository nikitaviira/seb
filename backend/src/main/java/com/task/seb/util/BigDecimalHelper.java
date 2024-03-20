package com.task.seb.util;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

public class BigDecimalHelper {
  public static final int PRESENTATION_SCALE = 3;
  public static final int PRESENTATION_SCALE_SMALL = 6;
  public static final int CURRENCY_SCALE = 2;
  public static final int PERCENT_SCALE = 2;
  public static final int CALCULATION_SCALE = 12;

  public static BigDecimal calculatePercentageDifference(BigDecimal v1, BigDecimal v2) {
    if (v1.compareTo(ZERO) == 0) {
      return ZERO;
    }

    return v2.subtract(v1)
        .divide(v1, CALCULATION_SCALE, HALF_UP)
        .multiply(new BigDecimal(100))
        .setScale(PERCENT_SCALE, HALF_UP)
        .stripTrailingZeros();
  }

  public static BigDecimal displayRounding(BigDecimal value) {
    if (value.compareTo(ZERO) == 0) {
      return ZERO;
    }

    boolean isLessThanOne = value.intValue() == 0;
    return isLessThanOne
        ? value.setScale(PRESENTATION_SCALE_SMALL, HALF_UP).stripTrailingZeros()
        : value.setScale(PRESENTATION_SCALE, HALF_UP).stripTrailingZeros();
  }
}
