package com.codecorecix.ecommerce.maintenance.product.detail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDto {

  private Integer id;

  private String nombre;

  private String descripcion;

  private Integer productId;
}
