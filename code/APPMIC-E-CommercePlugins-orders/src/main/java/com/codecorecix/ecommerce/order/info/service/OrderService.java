package com.codecorecix.ecommerce.order.info.service;

import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface OrderService {

  /**
   * Method used to save the order.
   *
   * @return the OrderResponseDto.
   */
  GenericResponse<OrderResponseDto> saveOrder(final OrderRequestDto orderRequestDto);
}
