package com.codecorecix.ecommerce.order.info.api.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderDetailResponseDto implements Serializable {

  private Integer id;

  private Integer orderId;

  private Integer productId;

  private Integer quantity;

  private Double unitPrice;

  private Double totalPrice;
}
