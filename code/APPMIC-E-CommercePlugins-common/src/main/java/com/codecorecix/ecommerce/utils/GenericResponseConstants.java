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

  public static final String CONFLICT = "The resource already exists in the database";

  public static final String UNAVAILABLE_SERVICE = "The service is not available, please try again later.";

  public static final String RETRIEVE_CODE_MSG = "The code was successfully generated with oauth 2.1";

  public static final String BAD_REQUEST_MESSAGE = "Invalid request: incorrect parameters or missing required fields";

  public static final String ID_PROVIDED_ON_CREATE = "The ID should not be provided when creating a new entity";

  public static final String UNAUTHORIZED_MESSAGE = "The token is invalid or has been modified";

  public static final String ACCESS_DENIED_MESSAGE = "You do not have the necessary permissions";

  public static final String NOT_FOUND_MESSAGE = "The resource ID does not exist in the database";

  public static final String ERROR_INTERNAL_MESSAGE = "An unexpected internal server error occurred. Please try again later or contact support.";

  public static final String VIEW = "/view";

  public static final String DASH = " - ";

  public static final String COLON = ": ";

  public static final String PERIOD = ".";

  public static final String COMMA = ", ";

  public static final String SPACE = " ";
}
