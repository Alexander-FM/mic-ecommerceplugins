package com.codecorecix.ecommerce.maintenance.category.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

  private Integer id;

  private String description;

  private Boolean isActive;
}
