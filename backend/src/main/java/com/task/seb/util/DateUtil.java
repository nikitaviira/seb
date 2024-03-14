package com.task.seb.util;

import java.time.Instant;
import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

public class DateUtil {
  private static final ThreadLocal<Instant> mockNow = new ThreadLocal<>();

  public static void setMockNow(Instant now) {
    mockNow.set(now);
  }

  public static void resetMockNow() {
    mockNow.remove();
  }

  public static Instant now() {
    Instant now = mockNow.get();
    return now != null ? now : Instant.now();
  }

  public static LocalDate today() {
    return LocalDate.ofInstant(now(), UTC);
  }
}
