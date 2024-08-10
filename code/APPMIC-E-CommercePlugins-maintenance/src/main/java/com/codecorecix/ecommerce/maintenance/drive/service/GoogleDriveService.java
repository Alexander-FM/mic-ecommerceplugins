package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleDriveService {

  /**
   * Méthod for upload an file to Google Drive.
   *
   * @param filePath the file path.
   * @param mimeType the mimeType.
   * @param folderName the folderName.
   * @return String the file url.
   * @throws GeneralSecurityException the GeneralSecurityException.
   * @throws IOException the IOException.
   */
  String uploadFile(final File filePath, final String mimeType, final String folderName) throws GeneralSecurityException, IOException;
}
