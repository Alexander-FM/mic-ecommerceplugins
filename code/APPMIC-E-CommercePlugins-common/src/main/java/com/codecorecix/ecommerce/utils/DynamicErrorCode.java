package com.codesoft.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class DynamicErrorCode implements IErrorCode {

  private final Integer errorCode;

  private final String errorMessage;

  private final HttpStatus httpStatus;

  public static DynamicErrorCode fromOriginal(final Integer originalCode, final String originalMessage, final HttpStatus httpStatus) {
    final Integer code = originalCode != null ? originalCode : GenericResponseConstants.RESPONSE_ERROR;
    final String msg = originalMessage != null ? originalMessage : GenericResponseConstants.WRONG_OPERATION;
    final HttpStatus status = httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
    return DynamicErrorCode.builder()
      .errorCode(code)
      .errorMessage(msg)
      .httpStatus(status)
      .build();
  }
}
