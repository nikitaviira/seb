package com.task.seb.domain.rate;

import com.task.seb.util.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
  List<CurrencyRate> findByQuoteAndDateGreaterThanEqual(Currency quote, LocalDate date);
  Optional<CurrencyRate> findFirstByQuoteOrderByDateDesc(Currency quote);
  long countByQuote(Currency quote);
}
