package com.codecorecix.ecommerce.order.info.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.service.OrderService;
import com.codecorecix.ecommerce.order.info.utils.OrderConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.OrdersUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.order-info}")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

  private final OrderService service;

  private final Environment environment;

  @PostMapping
  public ResponseEntity<GenericResponse<OrderResponseDto>> saveOrder(@RequestBody final OrderRequestDto orderRequestDto,
      @RequestHeader(value = "Token-External") final String token) {
    log.info("You are using the value: {}", environment.getProperty("app.external.maintenance-service-url"));
    if (ObjectUtils.isNotEmpty(orderRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(OrderConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      OrdersUtils.validRequestDto(orderRequestDto);
      OrdersUtils.validRequestDto(orderRequestDto.getOrderDetails());
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveOrder(orderRequestDto, token));
    }
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<OrderResponseDto>>> getAllOrders() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getAllOrders());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<OrderResponseDto>> getOrderById(@PathVariable(value = "id") final Long id) {
    final GenericResponse<OrderResponseDto> response = this.service.getOrderById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
