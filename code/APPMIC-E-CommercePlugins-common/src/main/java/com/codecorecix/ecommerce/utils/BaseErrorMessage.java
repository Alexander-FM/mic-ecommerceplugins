package com.codesoft.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseErrorMessage implements IErrorCode {
  BAD_REQUEST(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.BAD_REQUEST, 1),
    HttpStatus.BAD_REQUEST, GenericResponseConstants.BAD_REQUEST_MESSAGE),
  ID_PROVIDED_ON_CREATE(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.BAD_REQUEST, 2),
    HttpStatus.BAD_REQUEST, GenericResponseConstants.ID_PROVIDED_ON_CREATE),
  UNAUTHORIZED(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.UNAUTHORIZED, 1),
    HttpStatus.UNAUTHORIZED, GenericResponseConstants.UNAUTHORIZED_MESSAGE),
  ACCESS_DENIED(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.FORBIDDEN, 1),
    HttpStatus.FORBIDDEN, GenericResponseConstants.ACCESS_DENIED_MESSAGE),
  NOT_FOUND(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.NOT_FOUND, 1),
    HttpStatus.NOT_FOUND, GenericResponseConstants.NOT_FOUND_MESSAGE),
  ERROR_INTERNAL(GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, 1),
    HttpStatus.INTERNAL_SERVER_ERROR, GenericResponseConstants.ERROR_INTERNAL_MESSAGE),
  SERVICE_NOT_AVAILABLE(
    GenericResponseUtils.makeCode(GenericResponseConstants.APPMIC_COMMONS_ERROR_CODE, HttpStatus.SERVICE_UNAVAILABLE, 1),
    HttpStatus.SERVICE_UNAVAILABLE, GenericResponseConstants.UNAVAILABLE_SERVICE_MESSAGE);

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
}
