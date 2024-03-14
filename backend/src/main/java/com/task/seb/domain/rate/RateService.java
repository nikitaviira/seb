package com.task.seb.domain.rate;

import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RateService {
    private static final String RATE_CACHE_NAME = "rate";
    private final CurrencyRateRepository currencyRateRepository;

    @Cacheable(value = RATE_CACHE_NAME, key = "#quote", unless = "#result == null")
    public Optional<CurrencyRate> getLatestRate(Currency quote) {
        return currencyRateRepository.findFirstByQuoteOrderByDateDesc(quote);
    }

    @CacheEvict(value = RATE_CACHE_NAME, key = "#quote")
    public void evictRateCache(Currency quote) {}
}