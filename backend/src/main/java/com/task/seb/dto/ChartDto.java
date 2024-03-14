package com.task.seb.dto;

import java.math.BigDecimal;
import java.util.List;

public record ChartDto(
   List<ChartPointDto> chartPoints,
   BigDecimal changePercent
) {}
