package com.codecorecix.ecommerce.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse<T> {

  private Integer rpta;

  private String message;

  private T body;
}
