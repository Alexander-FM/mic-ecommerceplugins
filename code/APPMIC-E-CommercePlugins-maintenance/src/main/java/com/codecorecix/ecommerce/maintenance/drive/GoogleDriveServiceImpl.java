package com.codecorecix.ecommerce.maintenance.drive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

  private static final String APPLICATION_NAME = "APPMIC-E-CommercePlugins";

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

  private static final String CREDENTIALS_FILE_PATH = "/google-drive-credentials.json";

  public static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
    final InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (Objects.isNull(in)) {
      throw new FileNotFoundException(StringUtils.joinWith(GenericResponseConstants.COLON, GenericResponseConstants.RESOURCES_NOT_FOUND));
    }
    final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();

    final LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public static Drive getDriveService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
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
  public String uploadFile(java.io.File filePath, String mimeType, String folderName) throws GeneralSecurityException, IOException {
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
