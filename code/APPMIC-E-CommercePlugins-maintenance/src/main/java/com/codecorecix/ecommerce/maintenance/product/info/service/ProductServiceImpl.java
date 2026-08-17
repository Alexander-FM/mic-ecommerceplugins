package com.codecorecix.ecommerce.maintenance.product.info.service;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    // --- LÓGICA DE ACTUALIZACIÓN INTELIGENTE ---
    if (productRequestDto.getId() != null) {
      productRepository.findById(productRequestDto.getId()).ifPresent(existingProduct -> {
        log.info("Iniciando actualización del producto ID: {}. Comparando imágenes.", productRequestDto.getId());
        
        // 1. Comparar y limpiar la imagen principal
        if (StringUtils.isNotBlank(existingProduct.getMainImageUrl()) && !existingProduct.getMainImageUrl().equals(productRequestDto.getMainImageUrl())) {
          log.info("La imagen principal ha cambiado. Eliminando la antigua: {}", existingProduct.getMainImageUrl());
          deleteImageFromDriveByUrl(existingProduct.getMainImageUrl(), "principal antigua");
        }

        // 2. Comparar y limpiar las imágenes secundarias
        Set<String> oldImageUrls = Optional.ofNullable(existingProduct.getImages())
            .orElse(Collections.emptySet())
            .stream()
            .map(ProductImage::getImageUrl)
            .collect(Collectors.toSet());

        Set<String> newImageUrls = Optional.ofNullable(productRequestDto.getImages())
            .orElse(Collections.emptySet())
            .stream()
            .map(ProductImageRequestDto::getImageUrl)
            .collect(Collectors.toSet());

        // Imágenes a eliminar son las que estaban en la lista vieja pero ya no en la nueva
        Set<String> imagesToDelete = (Set<String>) CollectionUtils.subtract(oldImageUrls, newImageUrls);
        
        if (!imagesToDelete.isEmpty()) {
          log.info("Se eliminarán {} imágenes secundarias: {}", imagesToDelete.size(), imagesToDelete);
          imagesToDelete.forEach(url -> deleteImageFromDriveByUrl(url, "secundaria antigua"));
        }
      });
    }
    
    final Product productInfo = this.mapper.sourceToDestination(productRequestDto);
    
    try {
      final Product product = this.productRepository.save(productInfo);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.destinationToSource(product));
    } catch (Exception e) {
      // --- LÓGICA DE COMPENSACIÓN ---
      log.error("Error al guardar el producto en la base de datos: {}. Iniciando compensación.", e.getMessage());
      
      if (StringUtils.isNotBlank(productRequestDto.getMainImageUrl())) {
        deleteImageFromDriveByUrl(productRequestDto.getMainImageUrl(), "principal nueva");
      }
      
      if (productRequestDto.getImages() != null && !productRequestDto.getImages().isEmpty()) {
        productRequestDto.getImages().forEach(imageDto -> deleteImageFromDriveByUrl(imageDto.getImageUrl(), "secundaria nueva"));
      }
      
      throw e;
    }
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
      // Al eliminar un producto, sí borramos todas sus imágenes
      if (StringUtils.isNotBlank(productEntity.getMainImageUrl())) {
        deleteImageFromDriveByUrl(productEntity.getMainImageUrl(), "principal");
      }
      if (CollectionUtils.isNotEmpty(productEntity.getImages())) {
        productEntity.getImages().forEach(image -> deleteImageFromDriveByUrl(image.getImageUrl(), "secundaria"));
      }
      
      this.productRepository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } catch (Exception e) {
      log.error("Error eliminando producto: {}", e.getMessage());
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, e.getMessage()),
        null);
    }
  }
  
  private void deleteImageFromDriveByUrl(final String imageUrl, final String imageType) {
      try {
        final String fileId = extractFileIdFromGoogleDriveUrl(imageUrl);
        if (StringUtils.isNotBlank(fileId)) {
          this.googleDriveService.deleteFile(fileId);
          log.warn("LIMPIEZA/COMPENSACIÓN: Imagen {} ({}) eliminada de Google Drive: {}", imageType, fileId, imageUrl);
        }
      } catch (Exception e) {
        log.error("¡FALLO CRÍTICO EN LIMPIEZA/COMPENSACIÓN! No se pudo eliminar la imagen {} {}. Causa: {}", 
                  imageType, imageUrl, e.getMessage());
      }
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

  @Override
  public GenericResponse<List<ProductInfo>> findByCategoryId(final Integer categoryId) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
      this.mapper.toListDtoList(this.productRepository.findByCategoryId(categoryId)));
  }
}