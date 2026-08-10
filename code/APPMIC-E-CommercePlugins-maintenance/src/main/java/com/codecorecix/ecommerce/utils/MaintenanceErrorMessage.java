package com.codecorecix.ecommerce.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceErrorMessage {
  ERROR_NOT_FOUND_IMAGE(101, "An error occurred while retrieving the image URL, the image was not found in Google Drive"),
  ERROR_SAVING_IMAGE(102, "An error occurred while saving the image on Google Drive"),
  ERROR_UPDATE_IMAGE(103, "An error occurred while updating the image on Google Drive"),
  ERROR_DELETE_IMAGE(104, "An error occurred while deleting the image on Google Drive"),
  ERROR_SECURITY_GOOGLE_DRIVE(105, "Failed to create Drive service due to security error"),
  ERROR_RESOURCE_NOT_FOUND(106, "The resource does not exist"),
  ERROR_INTERNAL(107, "Failed to create Drive service due to security error"),
  ERROR_UNPROCESABLE_ENTITY(108, "You cannot send the ID when creating an object; check your request");

  private final Integer errorCode;

  private final String errorMessage;
}
