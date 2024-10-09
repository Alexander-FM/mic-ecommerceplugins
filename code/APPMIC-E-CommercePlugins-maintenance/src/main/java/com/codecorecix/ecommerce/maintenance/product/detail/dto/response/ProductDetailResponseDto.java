package com.codecorecix.ecommerce.maintenance.product.detail.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDto implements Serializable {

  private Integer id;

  private String name;

  private String description;

  private Integer productId;
}
