package com.codecorecix.ecommerce.maintenance.product.image.api.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDto implements Serializable {

  private Integer id;

  private String imageUrl;
}
