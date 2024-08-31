package com.codecorecix.ecommerce.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenericErrorMessage {
  NOT_FOUND_IMAGE(101, "The image url has not been found on Google Drive"),
  ERROR_SAVING_IMAGE(102, "An error occurred while saving the image on Google Drive"),
  ERROR_UPDATE_IMAGE(103, "An error occurred while updating the image on Google Drive"),
  ERROR_DELETE_IMAGE(104, "An error occurred while deleting the image on Google Drive"),
  INVALID_REQUEST_BODY(105, "The body of the request is not valid, check the fields length and try again"),
  DATABASE_SAVE_ERROR(106, "An error occurred while saving to the database."),
  DATABASE_UPDATE_ERROR(107, "An error occurred while updating to the database."),
  DATABASE_DELETE_ERROR(108, "An error occurred while deleting to the database."),
  DATABASE_LIST_ERROR(109, "An error occurred while fetching records from the database."),
  DATABASE_UPDATE_ERROR_PRODUCT(110,
      "An error occurred while updating the product in the database, please verify that the product.id is entered in the request.");

  private final Integer code;

  private final String message;
}
