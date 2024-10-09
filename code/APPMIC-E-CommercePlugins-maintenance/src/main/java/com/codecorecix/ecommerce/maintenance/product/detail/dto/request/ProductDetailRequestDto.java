package com.codecorecix.ecommerce.maintenance.product.detail.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailRequestDto implements Serializable {

  private Integer id;

  private String name;

  private String description;

  private Integer productId;
}
