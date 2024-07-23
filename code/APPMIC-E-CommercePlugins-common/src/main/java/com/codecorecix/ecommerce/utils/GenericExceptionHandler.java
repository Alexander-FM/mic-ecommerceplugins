package com.codecorecix.ecommerce.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericExceptionHandler {

  @ExceptionHandler(Exception.class)
  public GenericResponse<Exception> genericException(Exception ex) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_EXCEPTION, GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage()), null);
  }

  @ExceptionHandler(JDBCException.class)
  public GenericResponse<JDBCException> sqlException(JDBCException ex) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_SQL_EXCEPTION, -1,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage()), ex);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public GenericResponse<MethodArgumentNotValidException> sqlException(MethodArgumentNotValidException ex) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_VALID_EXCEPTION, -1,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_ERRONEA, ex.getMessage()), ex);
  }
}
