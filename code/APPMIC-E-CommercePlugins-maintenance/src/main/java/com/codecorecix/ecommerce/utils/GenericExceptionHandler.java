package com.codecorecix.ecommerce.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Exception> genericException(final Exception ex) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_EXCEPTION, GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage()), null);
  }

  @ExceptionHandler(JDBCException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<JDBCException> sqlException(final JDBCException ex) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_SQL_EXCEPTION, GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage()), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public GenericResponse<MethodArgumentNotValidException> validException(final MethodArgumentNotValidException ex) {
    final String defaultMessage =
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage());
    final StringBuilder errorMessageBuilder = new StringBuilder();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      String errorMessage = StringUtils.joinWith(GenericResponseConstants.COLON, GenericResponseConstants.OPERACION_ERRONEA,
          String.format("The field %s in the class %s say: %s", fieldError.getField(), fieldError.getObjectName(),
              fieldError.getDefaultMessage()));
      errorMessageBuilder.append(errorMessage).append(GenericResponseConstants.PERIOD);
    });
    return new GenericResponse<>(GenericResponseConstants.TIPO_VALID_EXCEPTION, GenericResponseConstants.RPTA_ERROR,
        StringUtils.defaultIfEmpty(errorMessageBuilder.toString(), defaultMessage), null);
  }
}
