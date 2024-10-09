package com.codecorecix.ecommerce.maintenance.customer.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerConstants {

  public static final String NO_EXIST = "The customer no exist in BD";

  public static final String FIND_MESSAGE = "The customer was found successfully";

  public static final String SAVE_MESSAGE = "The customer was saved successfully";

  public static final String UPDATE_MESSAGE = "The customer was updated successfully";

  public static final String DELETE_MESSAGE = "The customer was removed successfully";

  public static final String FIND_MESSAGE_ERROR = "An error occurred while finding the customer";

  public static final String SAVE_MESSAGE_ERROR = "An error occurred while saving the customer";

  public static final String UPDATE_MESSAGE_ERROR = "An error occurred while updating the customer";

  public static final String DELETE_MESSAGE_ERROR = "An error occurred while deleting the customer";
}
