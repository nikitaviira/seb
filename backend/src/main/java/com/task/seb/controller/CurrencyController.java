package com.task.seb.controller;

import com.task.seb.domain.rate.ChartService;
import com.task.seb.domain.rate.ChartType;
import com.task.seb.dto.ChartDto;
import com.task.seb.util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CurrencyController {
  private final ChartService chartService;

  @GetMapping("{quote}/historical-chart")
  public ChartDto historicalChart(@PathVariable Currency quote,
                                  @RequestParam(required = false, defaultValue = "ALL") ChartType chartType) {
    return chartService.historicalRateChartData(quote, chartType);
  }
}
