package com.codecorecix.ecommerce.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericExceptionHandler {

  private record ErrorResponse(Integer code, String message) {

  }

  @ExceptionHandler(GenericException.class)
  public ResponseEntity<Object> handleGenericException(final GenericException ex) {
    final HttpStatus httpStatus = switch (ex.getErrorMessage()) {
      case NOT_FOUND_IMAGE -> HttpStatus.NOT_FOUND;
      case DATABASE_SAVE_ERROR, DATABASE_UPDATE_ERROR, DATABASE_DELETE_ERROR, ERROR_SAVING_IMAGE, ERROR_DELETE_IMAGE, ERROR_UPDATE_IMAGE,
           DATABASE_LIST_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      case INVALID_REQUEST_BODY -> HttpStatus.BAD_REQUEST;
    };
    return new ResponseEntity<>(new ErrorResponse(ex.getCode(), ex.getMessage()), httpStatus);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Exception> genericException(final Exception ex) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.WRONG_OPERATION, ex.getMessage()), null);
  }

  @ExceptionHandler(JDBCException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<JDBCException> sqlException(final JDBCException ex) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.WRONG_OPERATION, ex.getMessage()), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public GenericResponse<MethodArgumentNotValidException> validException(final MethodArgumentNotValidException ex) {
    final String defaultMessage =
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.WRONG_OPERATION, ex.getMessage());
    final StringBuilder errorMessageBuilder = new StringBuilder();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      String errorMessage = StringUtils.joinWith(GenericResponseConstants.COLON, GenericResponseConstants.WRONG_OPERATION,
          String.format("The field %s in the class %s say: %s", fieldError.getField(), fieldError.getObjectName(),
              fieldError.getDefaultMessage()));
      errorMessageBuilder.append(errorMessage).append(GenericResponseConstants.PERIOD);
    });
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.defaultIfEmpty(errorMessageBuilder.toString(), defaultMessage), null);
  }
}
