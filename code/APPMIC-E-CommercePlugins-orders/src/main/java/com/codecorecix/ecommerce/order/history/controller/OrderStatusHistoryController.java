package com.codecorecix.ecommerce.order.history.controller;

import java.util.List;

import com.codecorecix.ecommerce.order.history.api.dto.response.OrderStatusHistoryResponseDto;
import com.codecorecix.ecommerce.order.history.service.OrderStatusHistoryService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.order-history}")
@RequiredArgsConstructor
public class OrderStatusHistoryController {

  private final OrderStatusHistoryService service;

  @GetMapping("/searchByOrderId/{orderId}")
  public ResponseEntity<GenericResponse<List<OrderStatusHistoryResponseDto>>> retrieveHistoryByOrderId(
      @PathVariable final Integer orderId) {
    final List<OrderStatusHistoryResponseDto> response = this.service.retrieveHistoryByOrderId(orderId);
    if (ObjectUtils.isNotEmpty(response)) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(GenericUtils.buildGenericResponseSuccess("Historial de estados de la orden", response));
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(GenericUtils.buildGenericResponseError("Historial de estados de la orden no encontrado", null));
    }
  }
}
