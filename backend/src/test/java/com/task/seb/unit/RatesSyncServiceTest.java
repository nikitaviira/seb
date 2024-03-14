package com.task.seb.unit;

import com.task.seb.client.FxRates;
import com.task.seb.client.FxRates.CurrencyAmount;
import com.task.seb.client.FxRates.FxRate;
import com.task.seb.client.RatesApiClient;
import com.task.seb.domain.rate.CurrencyRate;
import com.task.seb.domain.rate.CurrencyRateRepository;
import com.task.seb.domain.rate.RateService;
import com.task.seb.domain.rate.RatesSyncService;
import com.task.seb.util.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.task.seb.util.Currency.*;
import static com.task.seb.util.DateUtil.resetMockNow;
import static com.task.seb.util.DateUtil.setMockNow;
import static java.math.BigDecimal.ONE;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RatesSyncServiceTest {
    @Mock
    private CurrencyRateRepository currencyRateRepository;
    @Mock
    private RatesApiClient ratesApiClient;
    @Mock
    private RateService rateService;
    @InjectMocks
    private RatesSyncService ratesSyncService;

    @AfterEach
    void afterEach() {
        resetMockNow();
    }

    @Test
    void loadAndSaveHistoricalRates() {
        LocalDate today = LocalDate.of(2024, 3, 14);
        setMockNow(today.atStartOfDay().toInstant(UTC));
        Currency quote = USD;
        FxRates rates = createRates(today);

        when(ratesApiClient.getFxRatesForCurrency(eq(quote), eq(LocalDate.of(2021, 3, 14)), eq(today)))
            .thenReturn(rates);

        List<CurrencyRate> result = ratesSyncService.loadAndSaveHistoricalRates(quote);

        ArgumentCaptor<List<CurrencyRate>> captor = forClass(List.class);
        verify(currencyRateRepository).saveAll(captor.capture());

        List<CurrencyRate> captorValue = captor.getValue();
        assertThat(captorValue).containsExactly(rate("1.09", USD, today), rate("1.15", JPY, today.plusDays(1)));
    }

    @Test
    void loadAndSaveLatestRates() {
        LocalDate today = LocalDate.of(2024, 3, 14);
        FxRates rates = createRates(today);

        when(ratesApiClient.getCurrentFxRates()).thenReturn(rates);
        var oldRate = new CurrencyRate();
        oldRate.setDate(today.minusDays(1));
        when(rateService.getLatestRate(eq(USD))).thenReturn(Optional.of(oldRate));

        ratesSyncService.loadAndSaveLatestRates();

        verify(rateService).evictRateCache(USD);
        verify(rateService, never()).evictRateCache(JPY);

        ArgumentCaptor<List<CurrencyRate>> captor = forClass(List.class);
        verify(currencyRateRepository).saveAll(captor.capture());

        List<CurrencyRate> captorValue = captor.getValue();
        assertThat(captorValue).containsExactly(rate("1.09", USD, today));
    }

    private CurrencyRate rate(String rate, Currency quote, LocalDate date) {
        var currencyPairRate = new CurrencyRate();
        currencyPairRate.setRate(new BigDecimal(rate));
        currencyPairRate.setQuote(quote);
        currencyPairRate.setDate(date);
        return currencyPairRate;
    }

    private FxRates createRates(LocalDate date) {
        var fxRates = new FxRates();

        var fxRate1 = new FxRate();
        fxRate1.setDate(date);

        var currencyAmount1 = new CurrencyAmount();
        currencyAmount1.setCurrency(EUR);
        currencyAmount1.setAmount(ONE);

        var currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setCurrency(USD);
        currencyAmount2.setAmount(new BigDecimal("1.09"));

        fxRate1.setCurrencyAmounts(List.of(currencyAmount1, currencyAmount2));

        var fxRate2 = new FxRate();
        fxRate2.setDate(date.plusDays(1));

        var currencyAmount3 = new CurrencyAmount();
        currencyAmount3.setCurrency(EUR);
        currencyAmount3.setAmount(ONE);

        var currencyAmount4 = new CurrencyAmount();
        currencyAmount4.setCurrency(JPY);
        currencyAmount4.setAmount(new BigDecimal("1.15"));

        fxRate2.setCurrencyAmounts(List.of(currencyAmount3, currencyAmount4));

        fxRates.setRates(List.of(fxRate1, fxRate2));
        return fxRates;
    }
}
