package com.codecorecix.ecommerce.order.history.api.dto.request;

import java.time.LocalDateTime;

import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryRequestDto {

  private Integer id;

  private OrderRequestDto order;

  private OrderStatusRequestDto orderStatus;

  private LocalDateTime changedAt;

  private String changedBy;

  private String observation;

}
