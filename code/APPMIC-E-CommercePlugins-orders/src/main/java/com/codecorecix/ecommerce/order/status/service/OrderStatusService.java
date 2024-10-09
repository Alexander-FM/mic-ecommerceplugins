package com.codecorecix.ecommerce.order.status.service;

import java.util.List;

import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;
import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface OrderStatusService {

  /**
   * Method used to list all status.
   *
   * @return List of OrderStatusResponseDto.
   */
  GenericResponse<List<OrderStatusResponseDto>> listStatus();

  /**
   * Method used to save the status.
   *
   * @param orderStatusRequestDto The status request dto.
   * @return List of OrderStatusResponseDto.
   */
  GenericResponse<OrderStatusResponseDto> saveStatus(final OrderStatusRequestDto orderStatusRequestDto);

  /**
   * Method used to delete the status.
   *
   * @param id The id of the status.
   * @return List of OrderStatusResponseDto.
   */
  GenericResponse<OrderStatusResponseDto> deleteStatus(final Integer id);

  /**
   * Method used to desactive or activate the status.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of OrderStatusResponseDto.
   */
  GenericResponse<OrderStatusResponseDto> disabledOrEnabledStatus(final Boolean isActive, final Integer id);

  /**
   * Method used to find the status by id.
   *
   * @param id The id of the status.
   * @return List of OrderStatusResponseDto.
   */
  GenericResponse<OrderStatusResponseDto> findById(final Integer id);
}
