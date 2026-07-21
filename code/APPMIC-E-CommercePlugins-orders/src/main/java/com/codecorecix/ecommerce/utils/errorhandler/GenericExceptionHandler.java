package com.codecorecix.ecommerce.utils.errorhandler;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.exceptions.OrderException;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.OrderErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericResponse<Object>> handleAllExceptions(final Exception ex) {
    return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, ex.getMessage(), null),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(GenericUnprocessableEntityException.class)
  public ResponseEntity<GenericResponse<Object>> handleGenericUnprocessableEntityException(
      final GenericUnprocessableEntityException ex) {
    return new ResponseEntity<>(
        new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, ex.getMessage(), null),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(OrderException.class)
  public ResponseEntity<GenericResponse<Object>> handleOrderException(final OrderException ex) {
    OrderErrorMessage errorMessage = ex.getErrorMessage();
    HttpStatus status = switch (errorMessage) {
      case ERROR_RESOURCE_STATUS_NOT_AVAILABLE, ERROR_RESOURCE_ORDER_NOT_AVAILABLE, SERVICE_PRODUCTS_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case SERVICE_PRODUCTS_NOT_AVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
      case SERVICE_PRODUCTS_FORBIDDEN -> HttpStatus.FORBIDDEN;
      case SERVICE_PRODUCTS_NOT_AUTHORIZED, INVALID_TOKEN -> HttpStatus.UNAUTHORIZED;
      case INCONSISTENT_PRODUCT_DATA, PRODUCTS_OUT_OF_STOCK, INVALID_STATUS_TRANSITION -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
    return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, ex.getMessage(), null), status);
  }
}