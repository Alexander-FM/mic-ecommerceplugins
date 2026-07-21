package com.codecorecix.ecommerce.maintenance.product.info.api.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeRequestDto implements Serializable {

  private Integer attributeId;

  private String value;

}
