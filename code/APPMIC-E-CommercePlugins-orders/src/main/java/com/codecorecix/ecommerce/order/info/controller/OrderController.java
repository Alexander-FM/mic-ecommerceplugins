package com.codecorecix.ecommerce.order.info.controller;

import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.service.OrderService;
import com.codecorecix.ecommerce.order.info.utils.OrderConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService service;

  @PostMapping("/saveOrder")
  public ResponseEntity<GenericResponse<OrderResponseDto>> saveOrder(
      @Valid @RequestBody final OrderRequestDto orderRequestDto) {
    if (ObjectUtils.isNotEmpty(orderRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(OrderConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveOrder(orderRequestDto));
    }
  }
}
