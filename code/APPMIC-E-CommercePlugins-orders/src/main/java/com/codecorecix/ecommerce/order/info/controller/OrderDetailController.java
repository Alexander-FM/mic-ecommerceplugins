package com.codecorecix.ecommerce.order.info.controller;

import java.util.List;

import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;
import com.codecorecix.ecommerce.order.info.service.OrderDetailService;
import com.codecorecix.ecommerce.utils.GenericResponse;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.order-info}/details")
@RequiredArgsConstructor
public class OrderDetailController {

  private final OrderDetailService service;

  @GetMapping("/{orderId}")
  public ResponseEntity<GenericResponse<List<OrderDetailResponseDto>>> getDetailsByOrderId(
      @PathVariable(value = "orderId") final Integer orderId) {
    final GenericResponse<List<OrderDetailResponseDto>> response = this.service.getDetailsByOrderId(orderId);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
