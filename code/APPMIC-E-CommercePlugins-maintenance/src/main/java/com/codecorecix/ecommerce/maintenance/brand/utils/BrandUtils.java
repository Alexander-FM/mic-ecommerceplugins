package com.codecorecix.ecommerce.maintenance.brand.utils;

import com.codecorecix.ecommerce.maintenance.brand.api.dto.response.BrandResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandUtils {

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse of BrandResponseDto The generic response.
   */
  public static GenericResponse<BrandResponseDto> buildGenericResponseError() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, BrandConstants.NO_EXIST),
        null);
  }

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse of BrandResponseDto The generic response.
   */
  public static GenericResponse<BrandResponseDto> buildGenericResponseSuccess(final BrandResponseDto categoryResponseDto) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.CORRECT_OPERATION), categoryResponseDto);
  }
}
