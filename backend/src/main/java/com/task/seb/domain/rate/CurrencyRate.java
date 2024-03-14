package com.task.seb.domain.rate;

import com.task.seb.util.Currency;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.task.seb.util.Currency.EUR;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "currency_rates")
public class CurrencyRate {
  public static final Currency BASE_CURRENCY = EUR;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Enumerated(STRING)
  private Currency base = BASE_CURRENCY;

  @Enumerated(STRING)
  private Currency quote;

  private BigDecimal rate;

  private LocalDate date;
}