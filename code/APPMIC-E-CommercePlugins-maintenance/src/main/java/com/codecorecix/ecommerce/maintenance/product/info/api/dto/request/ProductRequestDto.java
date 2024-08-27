package com.codecorecix.ecommerce.maintenance.product.info.api.dto.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.event.entities.Brand;
import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.event.entities.ProductDetail;
import com.codecorecix.ecommerce.event.entities.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto implements Serializable {

  private Integer id;

  private String barCode;

  private String name;

  private String description;

  private Double price;

  private Integer stock;

  private Category category;

  private Brand brand;

  private Boolean isActive;

  private Boolean isRecommended;

  private List<ProductImage> images = new ArrayList<>();

  private List<ProductDetail> details = new ArrayList<>();
}
