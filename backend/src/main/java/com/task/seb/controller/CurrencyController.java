package com.task.seb.controller;

import com.task.seb.domain.rate.ChartService;
import com.task.seb.domain.rate.ChartType;
import com.task.seb.domain.rate.CurrencyConverterService;
import com.task.seb.dto.ChartDto;
import com.task.seb.dto.ConversionRequestDto;
import com.task.seb.dto.ConversionResultDto;
import com.task.seb.dto.CurrencyDto;
import com.task.seb.util.Currency;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.task.seb.util.Currency.ALL_CURRENCIES;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CurrencyController {
  private final ChartService chartService;
  private final CurrencyConverterService currencyConverterService;

  @GetMapping("{quote}/historical-chart")
  public ChartDto historicalChart(@PathVariable Currency quote,
                                  @RequestParam(required = false, defaultValue = "ALL") ChartType chartType) {
    return chartService.historicalRateChartData(quote, chartType);
  }

  @GetMapping("currency-list")
  public List<CurrencyDto> getCurrencyList() {
    return ALL_CURRENCIES.stream().map(c -> new CurrencyDto(c, c.getFullName())).toList();
  }

  @PostMapping("convert")
  public ConversionResultDto convert(@Valid @RequestBody ConversionRequestDto conversionRequest) {
    return currencyConverterService.convert(conversionRequest);
  }
}
