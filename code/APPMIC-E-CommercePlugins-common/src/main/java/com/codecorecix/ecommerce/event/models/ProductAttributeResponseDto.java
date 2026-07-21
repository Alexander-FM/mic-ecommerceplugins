package com.codecorecix.ecommerce.event.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeResponseDto implements Serializable {

  /**
   * Viene de Attribute.id
   */
  private Integer attributeId;

  /**
   * Viene de Attribute.name
   */
  private String name;

  /**
   * Viene de ProductAttribute.value
   */
  private String value;

}
