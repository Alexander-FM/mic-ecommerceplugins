package com.codecorecix.ecommerce.maintenance.product.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductConstants {

  public static final String NO_EXIST = "The product no exist in BD";

  public static final String UNPROCESSABLE_ENTITY_EXCEPTION = "The request is incorrectly formatted";
}
