package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.File;

import com.codecorecix.ecommerce.maintenance.drive.api.dto.response.GoogleDriveResponse;

public interface GoogleDriveService {

  /**
   * Method for upload a file to Google Drive.
   *
   * @param filePath the file path.
   * @param mimeType the mimeType.
   * @return GoogleDriveResponse the response.
   */
  GoogleDriveResponse uploadFile(final File filePath, final String mimeType);

  /**
   * Method for delete a file to Google Drive.
   *
   * @param fileId the file path.
   */
  void deleteFile(final String fileId);

  /**
   * Method for find a file to Google Drive.
   *
   * @param fileId the file path.
   */
  GoogleDriveResponse findFileByFileId(final String fileId);
}
