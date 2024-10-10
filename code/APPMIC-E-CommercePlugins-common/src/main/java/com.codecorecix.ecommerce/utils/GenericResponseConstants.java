package com.codecorecix.ecommerce.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericResponseConstants {

  public static final int RPTA_OK = 1;

  public static final int RPTA_WARNING = 0;

  public static final int RPTA_ERROR = -1;

  public static final String CORRECT_OPERATION = "Operation completed successfully";

  public static final String INCORRECT_OPERATION = "The operation could not be completed";

  public static final String WRONG_OPERATION = "An error occurred while performing the operation";

  public static final String RESOURCES_NOT_FOUND = "File not found";

  public static final String ORIGINAL_URL = "https://drive.google.com/file/d/";

  public static final String UNPROCESSABLE_ENTITY_EXCEPTION = "The request is incorrectly formatted";

  public static final String VIEW = "/view";

  public static final String DASH = " - ";

  public static final String COLON = ": ";

  public static final String PERIOD = ".";

  public static final String COMMA = ", ";

  public static final String SPACE = " ";
}
