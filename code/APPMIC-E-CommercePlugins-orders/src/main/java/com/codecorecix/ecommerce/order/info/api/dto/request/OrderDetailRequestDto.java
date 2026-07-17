package com.codecorecix.ecommerce.order.info.api.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailRequestDto implements Serializable {

  private Integer id;

  @NotNull(message = "The orderId is null, please fill")
  private Integer orderId;

  @NotNull(message = "The productId is null, please fill")
  private Integer productId;

  private String productName;

  private String productImageUrl;

  @NotNull(message = "The quantity is null, please fill")
  private Integer quantity;

  @NotNull(message = "The unitPrice is null, please fill")
  private Double unitPrice;
}
