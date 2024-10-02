package com.codecorecix.ecommerce.maintenance.product.image.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericException;
import com.codecorecix.ecommerce.maintenance.drive.api.dto.response.GoogleDriveResponse;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.service.ProductImageService;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.utils.GenericErrorMessage;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;
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
@RequestMapping("api/productImage")
@RequiredArgsConstructor
public class ProductImageController {

  public static final String MESSAGE = "The product id hasn't been found in the database";

  private final GoogleDriveService googleDriveService;

  private final ProductImageService productImageService;

  private final ProductService productService;

  private final ProductFieldsMapper productFieldsMapper;

  @PostMapping("/uploadImageProduct")
  public ResponseEntity<GenericResponse<ProductImageResponseDto>> uploadImage(@RequestParam("file") final MultipartFile file,
      @RequestParam("productId") final Integer productId) {
    try {
      final Path tempDir = Files.createTempDirectory(StringUtils.EMPTY);
      final Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
      Files.write(tempFilePath, file.getBytes());
      final GenericResponse<ProductResponseDto> productResponse = this.productService.findById(productId);
      if (Objects.nonNull(productResponse.getBody())) {
        final GoogleDriveResponse googleDriveResponse = this.googleDriveService.uploadFile(tempFilePath.toFile(), file.getContentType());
        Files.delete(tempFilePath);
        Files.delete(tempDir);
        final ProductImageRequestDto productImageRequestDto = this.buildProductImage(file, productId,
            StringUtils.join(GenericResponseConstants.ORIGINAL_URL, googleDriveResponse.getUrl(), GenericResponseConstants.VIEW));
        final GenericResponse<ProductImageResponseDto> productImageResponseDto =
            GenericUtils.buildGenericResponseSuccess(null, this.productImageService.saveImage(productImageRequestDto));
        return ResponseEntity.status(HttpStatus.OK).body(productImageResponseDto);
      } else {
        Files.delete(tempFilePath);
        Files.delete(tempDir);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericUtils.buildGenericResponseError(MESSAGE, null));
      }
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.ERROR_SAVING_IMAGE);
    }
  }

  @DeleteMapping("/deleteImageProduct/{fileId}")
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
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.ERROR_DELETE_IMAGE);
    }
  }

  /**
   * Méthod for build the product images request.
   *
   * @param file the field.
   * @param productId the product id.
   * @param urlFile the url file get from Google Drive api.
   * @return ProductImageRequestDto built.
   */
  private ProductImageRequestDto buildProductImage(final MultipartFile file, final Integer productId, final String urlFile) {
    return new ProductImageRequestDto(null, urlFile, productId, file);
  }
}
