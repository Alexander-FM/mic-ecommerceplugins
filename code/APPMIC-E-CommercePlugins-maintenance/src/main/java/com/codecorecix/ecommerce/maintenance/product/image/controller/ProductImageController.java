package com.codecorecix.ecommerce.maintenance.product.image.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exceptions.MaintenanceException;
import com.codecorecix.ecommerce.maintenance.drive.api.dto.response.GoogleDriveResponse;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.service.ProductImageService;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;
import com.codecorecix.ecommerce.utils.MaintenanceErrorMessage;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${app.endpoints.product-image}")
@RequiredArgsConstructor
public class ProductImageController {

  public static final String MESSAGE = "The product id hasn't been found in the database";

  private final GoogleDriveService googleDriveService;

  private final ProductImageService productImageService;

  private final ProductService productService;

  private final ProductFieldsMapper productFieldsMapper;

  @PostMapping("/bulk-upload")
  public ResponseEntity<GenericResponse<List<ProductImageResponseDto>>> uploadImage(@RequestParam("files") final List<MultipartFile> files,
    @RequestParam("productId") final Integer productId) {
    try {
      // Validate if the product exists in the database
      final GenericResponse<ProductResponseDto> productResponse = this.productService.findById(productId);
      List<ProductImageResponseDto> uploadedImages = new ArrayList<>();
      for (MultipartFile file : files) {
        final Path tempDir = Files.createTempDirectory(StringUtils.EMPTY);
        final Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(tempFilePath, file.getBytes());
        if (Objects.nonNull(productResponse.getBody())) {
          final GoogleDriveResponse googleDriveResponse = this.googleDriveService.uploadFile(tempFilePath.toFile(), file.getContentType());
          Files.delete(tempFilePath);
          Files.delete(tempDir);
          final ProductImageRequestDto productImageRequestDto = new ProductImageRequestDto(null,
            StringUtils.join(GenericResponseConstants.ORIGINAL_URL, googleDriveResponse.getUrl(), GenericResponseConstants.VIEW),
            productId);
          uploadedImages.add(this.productImageService.saveImage(productImageRequestDto));
        } else {
          Files.delete(tempFilePath);
          Files.delete(tempDir);
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericUtils.buildGenericResponseError(MESSAGE, null));
        }
      }
      return ResponseEntity.status(HttpStatus.CREATED).body(GenericUtils.buildGenericResponseSuccess(null, uploadedImages));
    } catch (final MaintenanceException e) {
      throw new MaintenanceException(e.getErrorMessage());
    } catch (IOException e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_RESOURCE_NOT_FOUND);
    }
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<GenericResponse<ProductImageResponseDto>> deleteImage(@PathVariable(name = "fileId") final String fileId) {
    try {
      final ProductImageResponseDto productImageResponseDto = this.productImageService.findByUrlName(fileId);
      if (StringUtils.isNotEmpty(productImageResponseDto.getImageUrl())) {
        this.googleDriveService.deleteFile(fileId);
        this.productImageService.deleteImage(productImageResponseDto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(GenericUtils.buildGenericResponseSuccess(null, null));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          GenericUtils.buildGenericResponseSuccess(null, null));
      }
    } catch (final MaintenanceException e) {
      throw new MaintenanceException(MaintenanceErrorMessage.ERROR_DELETE_IMAGE);
    }
  }
}
