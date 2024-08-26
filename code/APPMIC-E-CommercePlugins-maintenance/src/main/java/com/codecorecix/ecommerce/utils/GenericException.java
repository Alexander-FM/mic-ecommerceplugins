package com.codecorecix.ecommerce.utils;

import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

  private final GenericErrorMessage errorMessage;

  private final Integer code;

  public GenericException(final GenericErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
    this.code = errorMessage.getCode();
  }
}
