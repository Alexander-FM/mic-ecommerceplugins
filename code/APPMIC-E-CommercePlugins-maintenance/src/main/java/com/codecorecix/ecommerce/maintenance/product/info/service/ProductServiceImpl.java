package com.codecorecix.ecommerce.maintenance.product.info.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.info.repository.ProductRepository;
import com.codecorecix.ecommerce.maintenance.product.info.utils.ProductConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final GoogleDriveService googleDriveService;

  private final ProductRepository productRepository;

  private final ProductFieldsMapper mapper;

  @Override
  public GenericResponse<List<ProductInfo>> getAllProducts() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
      this.mapper.toListDtoList(this.productRepository.findAll()));
  }

  @Override
  public GenericResponse<List<ProductInfo>> getActiveProducts() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
      this.mapper.toListDtoList(this.productRepository.findByIsActiveIsTrue()));
  }

  @Override
  @Transactional
  public GenericResponse<ProductResponseDto> save(final ProductRequestDto productRequestDto) {
    final Product productInfo = this.mapper.sourceToDestination(productRequestDto);
    final Product product = this.productRepository.save(productInfo);
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
      this.mapper.destinationToSource(product));
  }

  @Override
  @Transactional
  public GenericResponse<ProductResponseDto> deleteProductById(final Integer id) {
    final Optional<Product> product = this.productRepository.findById(id);
    if (product.isEmpty()) {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION,
          ProductConstants.FIND_MESSAGE_ERROR),
        null);
    }

    try {
      final Product productEntity = product.get();
      deleteProductImagesFromDrive(productEntity);
      this.productRepository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } catch (Exception e) {
      log.error("Error eliminando producto: {}", e.getMessage());
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, e.getMessage()),
        null);
    }
  }

  private void deleteProductImagesFromDrive(final Product productEntity) {
    if (productEntity.getImages() == null || productEntity.getImages().isEmpty()) {
      return;
    }

    productEntity.getImages().forEach(image -> {
      try {
        final String fileId = extractFileIdFromGoogleDriveUrl(image.getImageUrl());
        if (StringUtils.isNotBlank(fileId)) {
          this.googleDriveService.deleteFile(fileId);
          log.info("Imagen eliminada de Google Drive: {}", fileId);
        }
      } catch (Exception e) {
        log.warn("Error eliminando imagen de Google Drive: {}", e.getMessage());
      }
    });
  }

  private String extractFileIdFromGoogleDriveUrl(final String imageUrl) {
    if (StringUtils.isBlank(imageUrl)) {
      return StringUtils.EMPTY;
    }

    final String fileIdFromViewUrl = StringUtils.substringBetween(imageUrl, "/file/d/", "/view");
    if (StringUtils.isNotBlank(fileIdFromViewUrl)) {
      return fileIdFromViewUrl;
    }

    final String idValue = StringUtils.substringAfter(imageUrl, "id=");
    if (StringUtils.isNotBlank(idValue) && !StringUtils.equals(idValue, imageUrl)) {
      return StringUtils.substringBefore(idValue, "&");
    }

    return StringUtils.EMPTY;
  }

  @Override
  @Transactional
  public GenericResponse<ProductResponseDto> updateProductStatus(final Boolean isActive, final Integer id) {
    final Optional<Product> product = this.productRepository.findById(id);
    if (product.isPresent()) {
      this.productRepository.disabledOrEnabledProduct(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION,
          ProductConstants.FIND_MESSAGE_ERROR),
        null);
    }
  }

  @Override
  public GenericResponse<ProductResponseDto> findById(final Integer id) {
    final Optional<Product> product = this.productRepository.findByIdFull(id);
    return product.map(
        value -> GenericUtils.buildGenericResponseSuccess(ProductConstants.FIND_MESSAGE, this.mapper.destinationToSource(value)))
      .orElseGet(() -> GenericUtils.buildGenericResponseError(ProductConstants.FIND_MESSAGE_ERROR, null));
  }

  @Override
  public GenericResponse<List<ProductInfo>> findByIds(final List<Integer> ids) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
      this.mapper.toListDtoList(this.productRepository.findAllById(ids)));
  }
}
