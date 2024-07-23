package com.codecorecix.ecommerce.category.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryConstants {
  public static final String NO_EXIST = "The category no exist in BD";

  public static final String UNPROCESSABLE_ENTITY_EXCEPTION = "The request is incorrectly formatted";
}
