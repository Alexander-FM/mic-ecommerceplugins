package com.codecorecix.ecommerce.event.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {

  private Integer id;

  private String barCode;

  private String name;

  private String description;

  private Double price;

  private Integer stock;

  private String categoryName;

  private String brandName;
}
