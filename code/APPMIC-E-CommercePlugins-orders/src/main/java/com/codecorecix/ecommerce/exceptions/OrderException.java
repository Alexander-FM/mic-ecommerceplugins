package com.codecorecix.ecommerce.exceptions;

import com.codecorecix.ecommerce.utils.OrderErrorMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderException extends RuntimeException {

  private final OrderErrorMessage errorMessage;
  private final Integer errorCode;
  private final String details;

  public OrderException(final OrderErrorMessage errorMessage) {
    super(errorMessage.getErrorMessage());
    this.errorMessage = errorMessage;
    this.errorCode = errorMessage.getErrorCode();
    this.details = null;
  }

  public OrderException(final OrderErrorMessage errorMessage, final String details) {
    super(String.format(errorMessage.getErrorMessage(), details));
    this.errorMessage = errorMessage;
    this.errorCode = errorMessage.getErrorCode();
    this.details = details;
  }

  @Override
  public String getMessage() {
    if (details != null) {
      return String.format(errorMessage.getErrorMessage(), details);
    }
    return errorMessage.getErrorMessage();
  }
}