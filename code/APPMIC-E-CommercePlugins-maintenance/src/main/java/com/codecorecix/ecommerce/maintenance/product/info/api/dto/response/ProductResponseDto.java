package com.codecorecix.ecommerce.maintenance.product.info.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductImage;

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

  private List<ProductImage> images = new ArrayList<>();

  private List<ProductDetailResponseDto> details = new ArrayList<>();
}
