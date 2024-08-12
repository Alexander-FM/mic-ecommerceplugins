package com.codecorecix.ecommerce.maintenance.drive.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveResponse;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.service.ProductImageService;
import com.codecorecix.ecommerce.maintenance.product.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.service.ProductService;
import com.codecorecix.ecommerce.utils.GenericException;
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
@RequestMapping("api/googleDrive")
@RequiredArgsConstructor
public class GoogleDriveController {

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
      final GoogleDriveResponse googleDriveResponse = this.googleDriveService.uploadFile(tempFilePath.toFile(), file.getContentType());
      Files.delete(tempFilePath);
      Files.delete(tempDir);

      final GenericResponse<ProductResponseDto> productResponse = this.productService.findById(productId);
      if (Objects.nonNull(productResponse.getBody()) && StringUtils.isNotEmpty(googleDriveResponse.getUrl())) {
        final ProductRequestDto productRequestDto = new ProductRequestDto(productResponse.getBody().getId());
        final ProductImageRequestDto productImageRequestDto =
            new ProductImageRequestDto(null, googleDriveResponse.getUrl(), this.productFieldsMapper.sourceToDestination(productRequestDto));
        final GenericResponse<ProductImageResponseDto> productImageResponseDto =
            GenericUtils.buildGenericResponseSuccess(this.productImageService.saveImage(productImageRequestDto),
                GenericResponseConstants.OPERACION_CORRECTA, GenericResponseConstants.RPTA_OK);
        return ResponseEntity.status(HttpStatus.OK).body(productImageResponseDto);
      } else {
        final GenericResponse<ProductImageResponseDto> productImageResponseDto =
            GenericUtils.buildGenericResponseSuccess(new ProductImageResponseDto(productId, googleDriveResponse.getUrl()),
                GenericResponseConstants.OPERACION_ERRONEA, GenericResponseConstants.RPTA_WARNING);
        return ResponseEntity.status(HttpStatus.OK).body(productImageResponseDto);
      }
    } catch (final Exception e) {
      throw new GenericException(GenericResponseConstants.OPERACION_INCORRECTA, e);
    }
  }

  @DeleteMapping("/deleteImage/{fileId}")
  public ResponseEntity<GenericResponse<ProductImageResponseDto>> deleteImage(@PathVariable(name = "fileId") final String fileId) {
    try {
      final GoogleDriveResponse googleDriveResponse = this.googleDriveService.deleteFile(fileId);
      final ProductImageResponseDto productImageResponseDto = this.productImageService.findByUrlName(fileId);
      if (StringUtils.isNotEmpty(productImageResponseDto.getImageUrl())) {
        final ProductImageResponseDto productImageResponseDtoDeleted =
            this.productImageService.deleteImage(productImageResponseDto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
            GenericUtils.buildGenericResponseSuccess(productImageResponseDtoDeleted, GenericResponseConstants.OPERACION_CORRECTA,
                GenericResponseConstants.RPTA_WARNING));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            GenericUtils.buildGenericResponseSuccess(new ProductImageResponseDto(), GenericResponseConstants.OPERACION_INCORRECTA,
                GenericResponseConstants.RPTA_WARNING));
      }
    } catch (final Exception e) {
      throw new GenericException(GenericResponseConstants.OPERACION_INCORRECTA, e);
    }
  }
}
