package com.codecorecix.ecommerce.maintenance.drive.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

import com.codecorecix.ecommerce.exceptions.MaintenanceException;
import com.codecorecix.ecommerce.maintenance.drive.api.dto.response.GoogleDriveResponse;
import com.codecorecix.ecommerce.maintenance.drive.utils.GoogleDriveConstants;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.MaintenanceConfigBean;
import com.codecorecix.ecommerce.utils.MaintenanceErrorMessage;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveServiceImpl implements GoogleDriveService {

  private final MaintenanceConfigBean maintenanceConfigBean;

  private final Environment env;

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  @Override
  public GoogleDriveResponse uploadFile(final File file, final String mimeType) {
    final GoogleDriveResponse googleDriveResponse = new GoogleDriveResponse();
    try {
      Drive drive = createDriveService();
      com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
      fileMetaData.setName(file.getName());
      fileMetaData.setParents(Collections.singletonList(GoogleDriveConstants.FOLDER_ID));
      FileContent mediaContent = new FileContent(mimeType, file);
      com.google.api.services.drive.model.File uploadFile = drive.files().create(fileMetaData, mediaContent).execute();
      googleDriveResponse.setCustomUrl(
          StringUtils.join(GenericResponseConstants.ORIGINAL_URL, uploadFile.getId(), GenericResponseConstants.VIEW));
      googleDriveResponse.setUrl(uploadFile.getId());
      googleDriveResponse.setMessage(GoogleDriveConstants.SUCCESSFUL_LOAD);
    } catch (final MaintenanceException e) {
      log.info("Error: {}", e.getMessage());
      throw new MaintenanceException(e.getErrorMessage());
    } catch (IOException e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_INTERNAL);
    }
    return googleDriveResponse;
  }

  @Override
  public void deleteFile(final String fileId) {
    try {
      Drive drive = createDriveService();
      drive.files().get(fileId).execute();
      this.findFileByFileId(fileId);
      drive.files().delete(fileId).execute();
    } catch (final GoogleJsonResponseException e) {
      log.info("Error deleting: {}", e.getDetails().getMessage());
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_DELETE_IMAGE);
    } catch (Exception e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_DELETE_IMAGE);
    }
  }

  @Override
  public GoogleDriveResponse findFileByFileId(final String fileId) {
    try {
      Drive drive = createDriveService();
      com.google.api.services.drive.model.File file = drive.files().get(fileId).execute();
      return new GoogleDriveResponse(GoogleDriveConstants.IMAGE_FOUND, file.getId(),
          StringUtils.join(GenericResponseConstants.ORIGINAL_URL, file.getId(), GenericResponseConstants.VIEW));
    } catch (Exception e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_SAVING_IMAGE);
    }
  }

  private Drive createDriveService() {
    try {
      final InputStream in;
      final GoogleCredential credential;
      if (retrieveCredentialsByEnvironment()) {
        // From an ENV of Kubernetes
        final String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (Objects.isNull(credentialsPath) || ObjectUtils.isEmpty(credentialsPath)) {
          throw new MaintenanceException(MaintenanceErrorMessage.ERROR_RESOURCE_NOT_FOUND);
        }
        credential =
            GoogleCredential.fromStream(new FileInputStream(credentialsPath)).createScoped(Collections.singletonList(DriveScopes.DRIVE));
      } else {
        // From application.properties
        in = GoogleDriveService.class.getResourceAsStream(this.maintenanceConfigBean.getCredentialsPath());
        if (Objects.isNull(in) || ObjectUtils.isEmpty(in)) {
          throw new MaintenanceException(MaintenanceErrorMessage.ERROR_RESOURCE_NOT_FOUND);
        }
        credential = GoogleCredential.fromStream(in).createScoped(Collections.singletonList(DriveScopes.DRIVE));
      }
      return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential).build();
    } catch (final IOException e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_INTERNAL);
    } catch (final GeneralSecurityException e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_SECURITY_GOOGLE_DRIVE);
    }
  }

  private boolean retrieveCredentialsByEnvironment() {
    if (StringUtils.isNotBlank(env.getProperty("GOOGLE_APPLICATION_CREDENTIALS"))) {
      log.info("Credentials from Kubernetes secrets will be used.");
      return true;
    } else {
      log.info("Credentials will be used from application.properties");
      return false;
    }
  }
}
