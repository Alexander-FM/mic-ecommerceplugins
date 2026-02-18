package com.codecorecix.ecommerce.maintenance.product.info.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeRequestDto {

  private Integer attributeId;

  private String value;

}
