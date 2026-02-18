package com.codecorecix.ecommerce.maintenance.attribute.api.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeRequestDto implements Serializable {

  private Integer id;

  @NotBlank(message = "The attribute name should not be null or blank")
  @Size(min = 2, max = 100, message = "The attribute name must be between 2 and 100 characters")
  private String name;
}

