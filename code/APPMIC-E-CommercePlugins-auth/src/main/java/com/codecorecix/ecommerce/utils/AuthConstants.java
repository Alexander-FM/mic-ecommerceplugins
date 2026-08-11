package com.codecorecix.ecommerce.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstants {

  public static final String EMPLOYEE_SERVICE_UNAVAILABLE_MESSAGE = "Employee service unavailable (connection refused)";

  public static final String CUSTOMER_SERVICE_UNAVAILABLE_MESSAGE = "Customer service unavailable (connection refused)";

  public static final String USER_SERVICE_UNAVAILABLE_MESSAGE = "User service unavailable (connection refused)";

  public static final String ROLE_SERVICE_UNAVAILABLE_MESSAGE = "Role service unavailable (connection refused)";
}
