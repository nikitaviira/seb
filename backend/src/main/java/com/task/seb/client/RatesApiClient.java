package com.task.seb.client;

import com.task.seb.util.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@FeignClient(name = "rates-api", url = "${rates-api.base-url}")
public interface RatesApiClient {
    @GetMapping(value = "/getCurrentFxRates", consumes = APPLICATION_XML_VALUE)
    FxRates getCurrentFxRates(@RequestParam("tp") ExchangeRateType exchangeRateType);

    @GetMapping(value = "/getFxRatesForCurrency", consumes = APPLICATION_XML_VALUE)
    FxRates getFxRatesForCurrency(@RequestParam("tp") ExchangeRateType exchangeRateType,
                                  @RequestParam("ccy") Currency quote,
                                  @RequestParam("dtFrom") LocalDate from,
                                  @RequestParam("dtTo") LocalDate to);
}