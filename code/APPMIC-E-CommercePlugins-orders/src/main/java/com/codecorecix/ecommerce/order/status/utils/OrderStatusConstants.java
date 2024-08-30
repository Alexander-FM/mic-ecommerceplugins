package com.codecorecix.ecommerce.order.status.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderStatusConstants {

  public static final String NO_EXIST = "The status no exist in BD";

  public static final String UNPROCESSABLE_ENTITY_EXCEPTION = "The request is incorrectly formatted";
}
