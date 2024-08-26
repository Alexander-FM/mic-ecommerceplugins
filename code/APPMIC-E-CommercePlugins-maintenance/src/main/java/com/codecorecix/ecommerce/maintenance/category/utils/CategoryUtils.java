package com.codecorecix.ecommerce.maintenance.category.utils;

import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryUtils {

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse of CategoryResponseDto The generic response.
   */
  public static GenericResponse<CategoryResponseDto> buildGenericResponseError() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, CategoryConstants.NO_EXIST),
        null);
  }

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse of CategoryResponseDto The generic response.
   */
  public static GenericResponse<CategoryResponseDto> buildGenericResponseSuccess(final CategoryResponseDto categoryResponseDto) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.CORRECT_OPERATION), categoryResponseDto);
  }
}
