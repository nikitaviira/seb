package com.task.seb.client;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.task.seb.util.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "FxRates")
@Getter
@Setter
public class FxRates {
    @JacksonXmlProperty(localName = "FxRate")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<FxRate> rates = new ArrayList<>();

    @Getter
    @Setter
    public static class FxRate {
        @JacksonXmlProperty(localName = "Dt")
        private LocalDate date;
        @JacksonXmlProperty(localName = "CcyAmt")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<CurrencyAmount> currencyAmounts;
    }

    @Getter
    @Setter
    public static class CurrencyAmount {
        @JacksonXmlProperty(localName = "Ccy")
        private Currency currency;
        @JacksonXmlProperty(localName = "Amt")
        private BigDecimal amount;
    }
}