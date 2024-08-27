package com.codecorecix.ecommerce.maintenance.product.detail.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailRequestDto {

  private Integer id;

  private String nombre;

  private String descripcion;

  private Integer productId;
}
