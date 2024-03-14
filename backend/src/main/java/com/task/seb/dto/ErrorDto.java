package com.task.seb.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
  public String message;
  public Map<String, List<String>> fields = new HashMap<>();

  public ErrorDto(String message) {
    this.message = message;
  }
}