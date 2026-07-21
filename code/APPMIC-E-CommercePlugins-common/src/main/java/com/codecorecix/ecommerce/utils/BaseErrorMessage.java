package com.codecorecix.ecommerce.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseErrorMessage implements IErrorCode {
  BAD_REQUEST(makeCode(10, HttpStatus.BAD_REQUEST, 1),
      HttpStatus.BAD_REQUEST, GenericResponseConstants.BAD_REQUEST_MESSAGE),
  ID_PROVIDED_ON_CREATE(makeCode(10, HttpStatus.BAD_REQUEST, 2),
      HttpStatus.BAD_REQUEST, GenericResponseConstants.ID_PROVIDED_ON_CREATE),
  UNAUTHORIZED(makeCode(10, HttpStatus.UNAUTHORIZED, 1),
      HttpStatus.UNAUTHORIZED, GenericResponseConstants.UNAUTHORIZED_MESSAGE),
  ACCESS_DENIED(makeCode(10, HttpStatus.FORBIDDEN, 1),
      HttpStatus.FORBIDDEN, GenericResponseConstants.ACCESS_DENIED_MESSAGE),
  NOT_FOUND(makeCode(10, HttpStatus.NOT_FOUND, 1),
      HttpStatus.NOT_FOUND, GenericResponseConstants.NOT_FOUND_MESSAGE),
  ERROR_INTERNAL(makeCode(10, HttpStatus.INTERNAL_SERVER_ERROR, 1),
      HttpStatus.INTERNAL_SERVER_ERROR, GenericResponseConstants.ERROR_INTERNAL_MESSAGE),
  SERVICE_NOT_AVAILABLE(
      makeCode(10, HttpStatus.SERVICE_UNAVAILABLE, 1),
      HttpStatus.SERVICE_UNAVAILABLE, GenericResponseConstants.UNAVAILABLE_SERVICE);

  private final Integer errorCode;

  private final HttpStatus httpStatus;

  private final String errorMessage;

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public Integer getErrorCode() {
    return errorCode;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /**
   * Method to create error codes.
   *
   * @param moduleId Module identifier (2 digits).
   * @param status   HTTP status.
   * @param seq      Sequence number (3 digits).
   * @return Generated error code.
   */
  public static int makeCode(final int moduleId, final HttpStatus status, final int seq) {
    return moduleId * 1_000_000 + status.value() * 1_000 + seq;
  }
}
