package com.codecorecix.ecommerce.utils.errorhandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
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

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> constraintValidations(final ConstraintViolationException ex) {
    final Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    final List<String> errorMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    final Map<String, List<String>> listHashMap = Collections.singletonMap("violations", errorMessages);
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, GenericResponseConstants.WRONG_OPERATION, listHashMap);
  }
}
