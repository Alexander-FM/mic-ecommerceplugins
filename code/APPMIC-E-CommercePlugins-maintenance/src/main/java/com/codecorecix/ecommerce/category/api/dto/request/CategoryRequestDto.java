package com.codecorecix.ecommerce.category.api.dto.request;

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

  private String description;

  private Boolean isActive;
}
