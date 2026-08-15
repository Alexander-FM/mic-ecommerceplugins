package com.codecorecix.ecommerce.order.history.api.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryResponseDto {
  private Integer id;

  private Integer orderId;

  private Integer orderStatusId;

  private String orderStatusName;

  private LocalDateTime changedAt;

  private String changedBy;

  private String observation;
}
