package com.codecorecix.ecommerce.exception;

import com.codecorecix.ecommerce.utils.IErrorCode;

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
