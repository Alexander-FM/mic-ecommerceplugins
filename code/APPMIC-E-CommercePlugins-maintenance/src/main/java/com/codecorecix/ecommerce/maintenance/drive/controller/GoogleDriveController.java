package com.codecorecix.ecommerce.maintenance.drive.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.codecorecix.ecommerce.maintenance.drive.api.dto.response.GoogleDriveResponse;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.drive.utils.GoogleDriveConstants;
import com.codecorecix.ecommerce.maintenance.product.image.service.ProductImageService;
import com.codecorecix.ecommerce.maintenance.product.info.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.utils.GenericErrorMessage;
import com.codecorecix.ecommerce.utils.GenericException;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/googleDrive")
@RequiredArgsConstructor
public class GoogleDriveController {

  private final GoogleDriveService googleDriveService;

  private final ProductImageService productImageService;

  private final ProductService productService;

  private final ProductFieldsMapper productFieldsMapper;

  @PostMapping("/uploadImage")
  public ResponseEntity<GenericResponse<GoogleDriveResponse>> uploadImage(@RequestParam("file") final MultipartFile file) {
    try {
      final Path tempDir = Files.createTempDirectory(StringUtils.EMPTY);
      final Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
      Files.write(tempFilePath, file.getBytes());
      final GoogleDriveResponse googleDriveResponse = this.googleDriveService.uploadFile(tempFilePath.toFile(), file.getContentType());
      Files.delete(tempFilePath);
      Files.delete(tempDir);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(googleDriveResponse, GenericResponseConstants.CORRECT_OPERATION,
              GenericResponseConstants.RPTA_OK));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.ERROR_SAVING_IMAGE);
    }
  }

  @DeleteMapping("/deleteImage/{fileId}")
  public ResponseEntity<GenericResponse<GoogleDriveResponse>> deleteImage(@PathVariable(name = "fileId") final String fileId) {
    try {
      this.googleDriveService.deleteFile(fileId);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(new GoogleDriveResponse(GoogleDriveConstants.SUCCESSFUL_REMOVAL, null, null),
              GenericResponseConstants.CORRECT_OPERATION,
              GenericResponseConstants.RPTA_OK));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.ERROR_DELETE_IMAGE);
    }
  }

  @GetMapping("/getImage/{fileId}")
  public ResponseEntity<GenericResponse<GoogleDriveResponse>> getImageByFileId(@PathVariable(name = "fileId") final String fileId) {
    try {
      final GoogleDriveResponse response = this.googleDriveService.findFileByFileId(fileId);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(response, GenericResponseConstants.CORRECT_OPERATION, GenericResponseConstants.RPTA_OK));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.ERROR_DELETE_IMAGE);
    }
  }
}
