package com.codecorecix.ecommerce.order.info.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderConstants {

  public static final String NO_EXIST_PRODUCT_IN_BD = "Ups! Product not found by id: ";

  public static final String NO_EXIST_PRODUCT_ID = "The product does not exist in the database, check the request and try again";

  public static final String UNPROCESSABLE_ENTITY_EXCEPTION = "The request is incorrectly formatted";
}
