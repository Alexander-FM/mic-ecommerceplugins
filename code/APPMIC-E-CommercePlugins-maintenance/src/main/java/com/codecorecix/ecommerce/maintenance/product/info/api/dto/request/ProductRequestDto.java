package com.codecorecix.ecommerce.maintenance.product.info.api.dto.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.event.entities.Brand;
import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.event.entities.ProductDetail;
import com.codecorecix.ecommerce.event.entities.ProductImage;

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

  @NotEmpty(message = "The images list is empty, please fill")
  private List<ProductImage> images = new ArrayList<>();

  @NotEmpty(message = "The details list is empty, please fill")
  private List<ProductDetail> details = new ArrayList<>();
}
