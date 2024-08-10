package com.codecorecix.ecommerce.maintenance.product.image.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDto {

  private Integer id;

  private String imageUrl;
}
