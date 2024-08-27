package com.codecorecix.ecommerce.maintenance.brand.api.dto.request;

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
public class BrandRequestDto {

  private Integer id;

  @NotNull(message = "The brand should not be null")
  @Size(min = 2, max = 50, message = "The size must be between 2 and 50 characters")
  private String description;

  private Boolean isActive;
}
