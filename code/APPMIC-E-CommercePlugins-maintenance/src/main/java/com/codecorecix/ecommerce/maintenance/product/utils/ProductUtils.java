package com.codecorecix.ecommerce.maintenance.product.utils;

import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUtils {

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse of CategoryResponseDto The generic response.
   */
  public static GenericResponse<ProductResponseDto> buildGenericResponseError() {
    return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_INCORRECTA, ProductConstants.NO_EXIST),
        null);
  }

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse of CategoryResponseDto The generic response.
   */
  public static GenericResponse<ProductResponseDto> buildGenericResponseSuccess(final ProductResponseDto productResponseDto) {
    return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_CORRECTA),
        productResponseDto);
  }
}
