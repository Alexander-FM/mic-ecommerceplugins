package com.codecorecix.ecommerce.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericUtils {

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse The generic response of any type.
   */
  public static <T> GenericResponse<T> buildGenericResponseSuccess(final String message, final T object) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.CORRECT_OPERATION, message), object);
  }

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse The generic response of any type.
   */
  public static <T> GenericResponse<T> buildGenericResponseError(final String message, final T object) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, message), object);
  }

  /**
   * Method use to build generic response warning.
   *
   * @return GenericResponse The generic response of any type.
   */
  public static <T> GenericResponse<T> buildGenericResponseWarning(final String message, final T object) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_WARNING,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.WRONG_OPERATION, message), object);
  }

  /**
   * Method use to convert local date time to date.
   *
   * @param date the date.
   * @return LocalDateTime.
   */
  public static LocalDateTime convertToLocalDateTime(final Date date) {
    return date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  /**
   * Method use to convert local date time to date.
   *
   * @param localDateTime the local date time.
   * @return Date.
   */
  public static Date convertToDate(final LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }
}
