package com.codecorecix.ecommerce.order.history.service;

import java.util.List;

import com.codecorecix.ecommerce.order.history.api.dto.request.OrderStatusHistoryRequestDto;
import com.codecorecix.ecommerce.order.history.api.dto.response.OrderStatusHistoryResponseDto;

public interface OrderStatusHistoryService {

  /**
   * Metodo utilizado para listar el historial de estados de un pedido por su ID.
   *
   * @param orderId la orden ID
   * @return una lista con el historial de estados de la orden
   */
  List<OrderStatusHistoryResponseDto> retrieveHistoryByOrderId(final Integer orderId);

  /**
   * Metodo utilizado para guardar el historial de estados de un pedido.
   *
   * @param orderStatusHistoryRequestDto el historial de estados de la orden
   */
  void saveOrderStatusHistory(final OrderStatusHistoryRequestDto orderStatusHistoryRequestDto);
}
