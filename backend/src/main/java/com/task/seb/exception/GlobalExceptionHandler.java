package com.task.seb.exception;

import com.task.seb.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException e) {
    log.error("Unexpected error occurred while executing request: " + e.getMessage(), e);
    return status(INTERNAL_SERVER_ERROR).body(new ErrorDto("Something went wrong!"));
  }

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<ErrorDto> handleServiceException(ServiceException e) {
    return status(BAD_REQUEST).body(new ErrorDto(e.getMessage()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                HttpStatusCode status, WebRequest request) {
    Map<String, List<String>> errors = ex
        .getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(groupingBy(FieldError::getField, mapping(FieldError::getDefaultMessage, toList())));
    return status(BAD_REQUEST).body(new ErrorDto("Validation failed", errors));
  }
}