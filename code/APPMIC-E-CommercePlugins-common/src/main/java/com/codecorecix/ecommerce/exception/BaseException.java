package com.codesoft.exception;

import com.codesoft.utils.IErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

  private final transient IErrorCode errorCodeInterface;

  public BaseException(final IErrorCode errorCodeInterface) {
    super(errorCodeInterface.getErrorMessage());
    this.errorCodeInterface = errorCodeInterface;
  }
}
