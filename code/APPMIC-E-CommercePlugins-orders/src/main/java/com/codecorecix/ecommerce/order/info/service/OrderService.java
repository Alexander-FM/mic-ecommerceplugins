package com.codecorecix.ecommerce.order.info.service;

import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import java.util.List;

public interface OrderService {

  /**
   * Method used to save the order.
   *
   * @param orderRequestDto the order request dto.
   * @return the OrderResponseDto.
   */
  GenericResponse<OrderResponseDto> saveOrder(final OrderRequestDto orderRequestDto);

  /**
   * Method used to get all orders.
   *
   * @return a {@link GenericResponse} containing a list of {@link OrderResponseDto}.
   */
  GenericResponse<List<OrderResponseDto>> getAllOrders();

  /**
   * Method used to get an order by id.
   *
   * @param orderId the order id.
   * @return a {@link GenericResponse} containing an object of {@link OrderResponseDto}.
   */
  GenericResponse<OrderResponseDto> getOrderById(final Long orderId);

  /**
   * Method used to get orders by customer id.
   *
   * @param customerId the customer id.
   * @return a {@link GenericResponse} containing a list of {@link OrderResponseDto}.
   */
  GenericResponse<List<OrderResponseDto>> getOrdersByCustomerId(final Integer customerId);
}