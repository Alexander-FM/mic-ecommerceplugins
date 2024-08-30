package com.codecorecix.ecommerce.utils;

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
  public static <T> GenericResponse<T> buildGenericResponseSuccess(final T object, final String message, final Integer rpta) {
    return new GenericResponse<>(rpta, StringUtils.joinWith(GenericResponseConstants.DASH, message), object
    );
  }
}
