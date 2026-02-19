package com.codecorecix.ecommerce.maintenance.product.info.api.dto.request;

import java.io.Serializable;
import java.util.Set;

import com.codecorecix.ecommerce.event.entities.Brand;
import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @NotNull(message = "The barcode is null, please fill.")
  @NotEmpty(message = "The barcode is empty, please fill.")
  @Size(min = 1, max = 20, message = "The size must be between 2 and 50 characters.")
  private String barCode;

  @NotNull(message = "The name of product is null, please fill.")
  @NotEmpty(message = "The name of product is empty, please fill.")
  private String name;

  @NotNull(message = "The description of product is null, please fill.")
  @NotEmpty(message = "The description of product is empty, please fill.")
  private String description;

  @NotNull(message = "The price of product is null, please fill.")
  private Double price;

  @NotNull(message = "The stock of product is null, please fill.")
  private Integer stock;

  @NotNull(message = "The product must be associated with a category, please fill.")
  private Category category;

  @NotNull(message = "The product must be associated with a brand, please fill.")
  private Brand brand;

  @NotNull(message = "The field is active is null, please fill.")
  private Boolean isActive;

  @NotNull(message = "The field is recommended is null, please fill.")
  private Boolean isRecommended;

  @NotNull(message = "The field mainImageUrl is null, please fill.")
  private String mainImageUrl;

  @NotEmpty(message = "The product must have at least one attribute, please fill.")
  private Set<ProductAttributeRequestDto> attributes;

  private Set<ProductImageRequestDto> images;
}
