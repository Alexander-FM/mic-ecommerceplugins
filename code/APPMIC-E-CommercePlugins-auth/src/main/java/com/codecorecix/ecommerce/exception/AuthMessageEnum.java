package com.codecorecix.ecommerce.exception;

import com.codecorecix.ecommerce.utils.AuthConstants;
import com.codecorecix.ecommerce.utils.IErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthMessageEnum implements IErrorCode {
  AUTH_EMPLOYEE_SERVICE_UNAVAILABLE(
      makeCode(20, HttpStatus.SERVICE_UNAVAILABLE, 2),
      HttpStatus.SERVICE_UNAVAILABLE,
      AuthConstants.EMPLOYEE_SERVICE_UNAVAILABLE_MESSAGE),
  AUTH_CUSTOMER_SERVICE_UNAVAILABLE(
      makeCode(20, HttpStatus.SERVICE_UNAVAILABLE, 3),
      HttpStatus.SERVICE_UNAVAILABLE,
      AuthConstants.CUSTOMER_SERVICE_UNAVAILABLE_MESSAGE),
  AUTH_USER_SERVICE_UNAVAILABLE(
      makeCode(20, HttpStatus.SERVICE_UNAVAILABLE, 4),
      HttpStatus.SERVICE_UNAVAILABLE,
      AuthConstants.USER_SERVICE_UNAVAILABLE_MESSAGE),
  AUTH_ROLE_SERVICE_UNAVAILABLE(
      makeCode(20, HttpStatus.SERVICE_UNAVAILABLE, 4),
  HttpStatus.SERVICE_UNAVAILABLE,
  AuthConstants.ROLE_SERVICE_UNAVAILABLE_MESSAGE);

  private final int code;

  private final HttpStatus status;

  private final String message;

  @Override
  public String getErrorMessage() {
    return message;
  }

  @Override
  public Integer getErrorCode() {
    return code;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return status;
  }

  /**
   * Method to create error codes.
   *
   * @param moduleId Module identifier (2 digits).
   * @param status HTTP status.
   * @param seq Sequence number (3 digits).
   * @return Generated error code.
   */
  public static int makeCode(final int moduleId, final HttpStatus status, final int seq) {
    return moduleId * 1_000_000 + status.value() * 1_000 + seq;
  }
}
