package com.codecorecix.ecommerce.event.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto implements Serializable {

  private Integer id;

  private String barCode;

  private String name;

  private String description;

  private Double price;

  private Integer stock;

  private String categoryName;

  private String brandName;

  private Boolean isRecommended;

  private String mainImageUrl;

  private Set<ProductAttributeResponseDto> attributes = new HashSet<>();

  private Set<ProductImageResponseDto> images = new HashSet<>();
}
