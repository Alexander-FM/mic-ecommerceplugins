package com.codecorecix.ecommerce.maintenance.category.api.dto.request;

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
public class CategoryRequestDto {

  private Integer id;

  @NotNull(message = "The category should not be null")
  @Size(min = 2, max = 50, message = "The size must be between 2 and 50 characters")
  private String description;

  private Boolean isActive;

  private Integer parentCategory;
}
