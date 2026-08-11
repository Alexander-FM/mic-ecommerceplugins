package com.codecorecix.ecommerce.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstants {

  public static final String EMPLOYEE_SERVICE_UNAVAILABLE_MESSAGE = "Employee service unavailable (connection refused)";

  public static final String CUSTOMER_SERVICE_MESSAGE = "An error occurred while processing the request in the maintenance microservice";

  public static final String NOT_FOUND_MESSAGE = "The user not was found";

}
