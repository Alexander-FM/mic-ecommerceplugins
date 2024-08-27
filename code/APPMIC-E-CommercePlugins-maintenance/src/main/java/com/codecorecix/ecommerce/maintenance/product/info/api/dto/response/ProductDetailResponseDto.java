package com.codecorecix.ecommerce.maintenance.product.info.api.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDto implements Serializable {

  private String nombre;

  private String descripcion;
}
