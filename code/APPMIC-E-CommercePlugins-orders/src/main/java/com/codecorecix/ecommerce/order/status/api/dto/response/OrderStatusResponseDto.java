package com.codecorecix.ecommerce.order.status.api.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderStatusResponseDto implements Serializable {

  private Integer id;

  private String statusName;

  private Boolean isActive;
}
