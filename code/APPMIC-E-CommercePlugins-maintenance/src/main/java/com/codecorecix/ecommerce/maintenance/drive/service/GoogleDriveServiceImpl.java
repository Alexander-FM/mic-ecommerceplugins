package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

  private static final String APPLICATION_NAME = "APPMIC-E-CommercePlugins";

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive");

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  public Drive getDriveService() throws GeneralSecurityException, IOException {
    final InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (Objects.isNull(in)) {
      throw new FileNotFoundException(StringUtils.joinWith(GenericResponseConstants.COLON, GenericResponseConstants.RESOURCES_NOT_FOUND));
    }
    final GoogleCredentials credentials = ServiceAccountCredentials.fromStream(in).createScoped(SCOPES);
    return new Drive.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JSON_FACTORY,
        new HttpCredentialsAdapter(credentials))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  private String getOrCreateFolderId(String folderName, Drive driveService) throws IOException {
    String folderId = null;
    FileList result = driveService.files().list()
        .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'")
        .setSpaces("drive")
        .setFields("files(id, name)")
        .execute();
    for (File file : result.getFiles()) {
      if (file.getName().equals(folderName)) {
        folderId = file.getId();
        break;
      }
    }

    if (folderId == null) {
      File fileMetadata = new File();
      fileMetadata.setName(folderName);
      fileMetadata.setMimeType("application/vnd.google-apps.folder");

      File file = driveService.files().create(fileMetadata)
          .setFields("id")
          .execute();
      folderId = file.getId();
    }
    return folderId;
  }

  @Override
  public String uploadFile(final java.io.File filePath, final String mimeType, final String folderName)
      throws GeneralSecurityException, IOException {
    Drive driveService = getDriveService();
    String folderId = getOrCreateFolderId(folderName, driveService);

    File fileMetadata = new File();
    fileMetadata.setName(filePath.getName());
    fileMetadata.setParents(Collections.singletonList(folderId));

    FileContent mediaContent = new FileContent(mimeType, filePath);
    File file = driveService.files().create(fileMetadata, mediaContent)
        .setFields("id, webViewLink")
        .execute();
    return file.getWebViewLink();
  }
}
