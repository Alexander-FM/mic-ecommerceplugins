package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleDriveServiceImpl implements GoogleDriveService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  @Override
  public GoogleDriveResponse uploadFile(final File file, final String mimeType) {
    final GoogleDriveResponse googleDriveResponse = new GoogleDriveResponse();
    try {
      final String folderId = "1I1t56zZveZub2phFIaNAoztIf3wH3Rxy";
      Drive drive = createDriveService();
      com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
      fileMetaData.setName(file.getName());
      fileMetaData.setParents(Collections.singletonList(folderId));
      FileContent mediaContent = new FileContent(mimeType, file);
      com.google.api.services.drive.model.File uploadFile = drive.files().create(fileMetaData, mediaContent).execute();
      googleDriveResponse.setUrl(
          StringUtils.join(GenericResponseConstants.ORIGINAL_URL, uploadFile.getId(), GenericResponseConstants.VIEW));
      googleDriveResponse.setMessage("Image load with successfully");
    } catch (final Exception e) {
      log.info("Error: {}", e.getMessage());
      googleDriveResponse.setStatus(500);
      googleDriveResponse.setMessage(e.getMessage());
    }
    return googleDriveResponse;
  }

  @Override
  public GoogleDriveResponse deleteFile(final String fileId) {
    final GoogleDriveResponse googleDriveResponse = new GoogleDriveResponse();
    try {
      Drive drive = createDriveService();
      drive.files().delete(fileId).execute();
      return new GoogleDriveResponse(1, "¡File has been deleted!", fileId);
    } catch (final Exception e) {
      log.info("Error deleting file: {}", e.getMessage());
      googleDriveResponse.setMessage("File hasn't been deleted!");
      googleDriveResponse.setStatus(0);
    }
    return googleDriveResponse;
  }

  private Drive createDriveService() throws IOException, GeneralSecurityException {
    final InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (Objects.isNull(in)) {
      throw new FileNotFoundException(StringUtils.joinWith(GenericResponseConstants.COLON, GenericResponseConstants.RESOURCES_NOT_FOUND));
    }
    GoogleCredential credential = GoogleCredential.fromStream(in).createScoped(Collections.singletonList(DriveScopes.DRIVE));
    return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential).build();
  }
}
