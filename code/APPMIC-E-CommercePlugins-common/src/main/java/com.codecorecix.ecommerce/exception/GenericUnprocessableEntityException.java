package com.codecorecix.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que mapea el código HTTP 422 - UNPROCESSABLE_ENTITY.
 */
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class GenericUnprocessableEntityException extends RuntimeException {

  /**
   * Instantiates a new unprocessable entity exception.
   *
   * @param message the message
   */
  public GenericUnprocessableEntityException(String message) {
    super(message);
  }
}
