package com.codecorecix.ecommerce.maintenance.product.image.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.mapper.ProductImageFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.image.repository.ProductImageRepository;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

  private final ProductImageRepository repository;

  private final ProductImageFieldsMapper mapper;

  @Override
  public ProductImageResponseDto saveImage(final ProductImageRequestDto productImageRequestDto) {
    final ProductImage productImage = this.repository.save(this.mapper.sourceToDestination(productImageRequestDto));
    return this.mapper.destinationToSource(productImage);
  }

  @Override
  public ProductImageResponseDto deleteImage(final Integer id) {
    final Optional<ProductImage> productImage = this.repository.findById(id);
    if (productImage.isPresent()) {
      this.repository.deleteById(id);
      return ProductImageResponseDto.builder().build();
    } else {
      return ProductImageResponseDto.builder().id(id).build();
    }
  }

  @Override
  public ProductImageResponseDto findById(final Integer id) {
    final Optional<ProductImage> productImage = this.repository.findById(id);
    return productImage.map(this.mapper::destinationToSource).orElseGet(ProductImageResponseDto::new);
  }

  @Override
  public ProductImageResponseDto findByUrlName(final String urlName) {
    final String buildUrl = StringUtils.join(GenericResponseConstants.ORIGINAL_URL, urlName, GenericResponseConstants.VIEW);
    final Optional<ProductImage> productImage = this.repository.findProductImageByImageUrl(buildUrl);
    return productImage.map(this.mapper::destinationToSource).orElseGet(ProductImageResponseDto::new);
  }

  @Override
  public List<ProductImageResponseDto> findByUrlProductId(final Integer productId) {
    final List<ProductImage> productImage = this.repository.findProductImagesByProductId(productId);
    if (ObjectUtils.isNotEmpty(productImage)) {
      return this.mapper.toDto(productImage);
    } else {
      return new ArrayList<>();
    }
  }
}
