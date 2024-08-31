package com.codecorecix.ecommerce.event.models;

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

  private String nombre;

  private String descripcion;

  private Integer productId;
}
