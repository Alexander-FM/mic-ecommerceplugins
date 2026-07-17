package com.codecorecix.ecommerce.exceptions;

import com.codecorecix.ecommerce.utils.OrderErrorMessage;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

  private final OrderErrorMessage errorMessage;
  private final Integer errorCode;

  public OrderException(final OrderErrorMessage errorMessage) {
    super(errorMessage.getErrorMessage());
    this.errorMessage = errorMessage;
    this.errorCode = errorMessage.getErrorCode();
  }

  public OrderException(final OrderErrorMessage errorMessage, final Object... args) {
    super(String.format(errorMessage.getErrorMessage(), args));
    this.errorMessage = errorMessage;
    this.errorCode = errorMessage.getErrorCode();
  }
}