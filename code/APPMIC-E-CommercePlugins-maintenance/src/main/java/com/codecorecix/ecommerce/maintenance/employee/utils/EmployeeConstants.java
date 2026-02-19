package com.codecorecix.ecommerce.maintenance.employee.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeConstants {

  public static final String NO_EXIST = "The employee no exist in BD";

  public static final String FIND_MESSAGE = "The employee was found successfully";

  public static final String SAVE_MESSAGE = "The employee was saved successfully";

  public static final String UPDATE_MESSAGE = "The employee was updated successfully";

  public static final String DELETE_MESSAGE = "The employee was removed successfully";

  public static final String FIND_MESSAGE_ERROR = "An error occurred while finding the employee";

  public static final String SAVE_MESSAGE_ERROR = "An error occurred while saving the employee";

  public static final String UPDATE_MESSAGE_ERROR = "An error occurred while updating the employee";

  public static final String DELETE_MESSAGE_ERROR = "An error occurred while deleting the employee";

  public static final String NOT_EXIST_USER_FOR_EMPLOYEE = "The user associated with the employee does not exist";

  public static final String EMPLOYEE_CONFLICT = "Already exist a employee with the same userId";

}
