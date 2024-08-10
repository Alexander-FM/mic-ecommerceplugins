package com.codecorecix.ecommerce.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericException extends RuntimeException {

  /**
   * Instantiates a new unprocessable entity exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public GenericException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new unprocessable entity exception.
   *
   * @param message the message
   */
  public GenericException(String message) {
    super(message);
  }

  /**
   * Instantiates a new unprocessable entity exception.
   *
   * @param cause the cause
   */
  public GenericException(Throwable cause) {
    super(cause);
  }
}
