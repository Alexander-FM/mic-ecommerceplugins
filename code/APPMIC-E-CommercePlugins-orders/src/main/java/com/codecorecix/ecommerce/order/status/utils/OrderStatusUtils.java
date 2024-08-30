package com.codecorecix.ecommerce.order.status.utils;

import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderStatusUtils {

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse of OrderStatusResponseDto The generic response.
   */
  public static GenericResponse<OrderStatusResponseDto> buildGenericResponseError() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, OrderStatusConstants.NO_EXIST),
        null);
  }

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse of OrderStatusResponseDto The generic response.
   */
  public static GenericResponse<OrderStatusResponseDto> buildGenericResponseSuccess(final OrderStatusResponseDto orderStatusResponseDto) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.CORRECT_OPERATION), orderStatusResponseDto);
  }
}
