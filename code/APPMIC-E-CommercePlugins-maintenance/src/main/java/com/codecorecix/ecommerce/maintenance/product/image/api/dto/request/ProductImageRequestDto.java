package com.codecorecix.ecommerce.maintenance.product.image.api.dto.request;

import com.codecorecix.ecommerce.event.entities.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageRequestDto {

  private Integer id;

  private String imageUrl;

  private Product product;
}
