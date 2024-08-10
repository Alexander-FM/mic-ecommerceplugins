package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleDriveService {

  /**
   * Méthod for upload a file to Google Drive.
   *
   * @param filePath the file path.
   * @param mimeType the mimeType.
   * @return GoogleDriveResponse the response.
   * @throws GeneralSecurityException the GeneralSecurityException.
   * @throws IOException the IOException.
   */
  GoogleDriveResponse uploadFile(final File filePath, final String mimeType) throws GeneralSecurityException, IOException;
}
