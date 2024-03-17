package com.task.seb.integration;

import com.task.seb.domain.rate.CurrencyRateRepository;
import com.task.seb.domain.rate.RateService;
import com.task.seb.dto.ConversionRequestDto;
import com.task.seb.integration.util.IntTestBase;
import com.task.seb.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.task.seb.Helper.rate;
import static com.task.seb.util.Currency.EUR;
import static com.task.seb.util.Currency.USD;
import static com.task.seb.util.DateUtil.setMockNow;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class CurrencyControllerTest extends IntTestBase {
  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private MockMvc mockMvc;
  @MockBean
  private RateService rateService;
  @Autowired
  private CurrencyRateRepository currencyRateRepository;

  @Test
  public void convert_success() throws Exception {
    Currency quote = USD;
    String body = convertObjectToJsonString(new ConversionRequestDto(EUR, quote, new BigDecimal(25)));
    when(rateService.getLatestRate(quote)).thenReturn(Optional.of(rate(LocalDate.now(), quote, "1.0813")));

    mockMvc.perform(post("/api/convert")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.conversionRate").value("1.0813"))
        .andExpect(jsonPath("$.conversionResult").value("27.03"));

    String reversedBody = convertObjectToJsonString(new ConversionRequestDto(quote, EUR, new BigDecimal(25)));
    mockMvc.perform(post("/api/convert")
            .content(reversedBody)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.conversionRate").value("0.924813"))
        .andExpect(jsonPath("$.conversionResult").value("23.12"));
  }

  @Test
  public void convert_validationError() throws Exception {
    String body = convertObjectToJsonString(new ConversionRequestDto(null, null, new BigDecimal("-54.241243243342")));

    mockMvc.perform(post("/api/convert")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.quote[0]").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.base[0]").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.amount", hasItems("Value must be more than zero", "Number out of bounds")));
  }

  @Test
  public void currencyList() throws Exception {
    mockMvc.perform(get("/api/currency-list"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(146))
        .andExpect(jsonPath("$[*].code", not(hasItem("UNKNOWN"))))
        .andExpect(jsonPath("$[*].code", not(hasItem("EUR"))))
        .andExpect(jsonPath("$[*].code", hasItem("USD")));
  }

  @Test
  public void historicalChart() throws Exception {
    Currency quote = USD;
    LocalDate today = LocalDate.of(2024, 3, 14);
    setMockNow(today.atStartOfDay().toInstant(UTC));

    currencyRateRepository.saveAll(List.of(
        rate(today, quote, "1.2"),
        rate(today.minusDays(10), quote, "1.4"),
        rate(today.minusMonths(1), quote, "1.5"),
        rate(today.minusMonths(1).minusDays(1), quote, "1.3"),
        rate(today.withDayOfYear(1), quote, "1.7"),
        rate(today.minusYears(1), quote, "1.8"),
        rate(today.minusYears(1).minusDays(1), quote, "1.9")
    ));

    String endpoint = "/api/%s/historical-chart".formatted(quote);
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.chartPoints.length()").value(6))
        .andExpect(jsonPath("$.changePercent").value("-33.33"))
        .andExpect(jsonPath("$.chartPoints[0].date").value("2023-03-14"))
        .andExpect(jsonPath("$.chartPoints[0].value").value("1.8"));

    mockMvc.perform(get(endpoint).queryParam("chartType", "ALL"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.chartPoints.length()").value(7))
        .andExpect(jsonPath("$.changePercent").value("-36.84"))
        .andExpect(jsonPath("$.chartPoints[0].date").value("2023-03-13"))
        .andExpect(jsonPath("$.chartPoints[0].value").value("1.9"));

    mockMvc.perform(get(endpoint).queryParam("chartType", "MONTH"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.chartPoints.length()").value(3))
        .andExpect(jsonPath("$.changePercent").value("-20"))
        .andExpect(jsonPath("$.chartPoints[0].date").value("2024-02-14"))
        .andExpect(jsonPath("$.chartPoints[0].value").value("1.5"));

    mockMvc.perform(get(endpoint).queryParam("chartType", "YTD"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.chartPoints.length()").value(5))
        .andExpect(jsonPath("$.changePercent").value("-29.41"))
        .andExpect(jsonPath("$.chartPoints[0].date").value("2024-01-01"))
        .andExpect(jsonPath("$.chartPoints[0].value").value("1.7"));
  }
}