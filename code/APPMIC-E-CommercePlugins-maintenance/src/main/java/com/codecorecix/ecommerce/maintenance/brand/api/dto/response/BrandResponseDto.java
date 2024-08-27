package com.codecorecix.ecommerce.maintenance.brand.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDto {

  private Integer id;

  private String description;

  private Boolean isActive;
}
