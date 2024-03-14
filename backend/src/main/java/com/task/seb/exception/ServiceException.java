package com.task.seb.exception;

public class ServiceException extends RuntimeException {
  public ServiceException(String message) {
    super(message);
  }
}