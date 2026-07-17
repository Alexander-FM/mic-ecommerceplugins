package com.codecorecix.ecommerce.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorMessage {
  INVALID_TOKEN(401, "The token is invalid or has been modified"),
  ERROR_RESOURCE_STATUS_NOT_AVAILABLE(404, "The order status ID does not exist in the database"),
  ERROR_RESOURCE_ORDER_NOT_AVAILABLE(404, "The order ID does not exist in the database"),
  ERROR_INTERNAL(500, "Internal Server Error"),
  SERVICE_PRODUCTS_NOT_AVAILABLE(503, "The product stock could not be verified, the service is not available"),
  SERVICE_PRODUCTS_NOT_AUTHORIZED(401, "The request to the product service is not authorized, verify the token"),
  SERVICE_PRODUCTS_FORBIDDEN(403, "Access to the product service is forbidden, verify the token"),
  SERVICE_PRODUCTS_NOT_FOUND(404, "Requested products were not found"),
  SERVICE_PRODUCTS_ENDPOINT_ERROR(500, "Invalid product service endpoint configuration"),
  INCONSISTENT_PRODUCT_DATA(400, "Some products in the request do not exist."),
  PRODUCTS_OUT_OF_STOCK(400, "The following products are out of stock: %s"),
  INVALID_STATUS_TRANSITION(400, "Invalid status transition from '%s' to '%s'.");

  private final Integer errorCode;

  private final String errorMessage;
}