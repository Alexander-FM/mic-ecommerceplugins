package com.codecorecix.ecommerce.order.info.service;

import java.util.List;

import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface OrderDetailService {
  /**
   * Method used to get details by orderId.
   *
   * @return a {@link GenericResponse} containing a list of {@link OrderResponseDto}.
   */
  GenericResponse<List<OrderDetailResponseDto>> getDetailsByOrderId(final Integer orderId);
}
