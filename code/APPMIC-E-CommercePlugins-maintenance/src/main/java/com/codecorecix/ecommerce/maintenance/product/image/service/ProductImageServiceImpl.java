package com.codecorecix.ecommerce.maintenance.product.image.service;

import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.mapper.ProductImageFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.image.repository.ProductImageRepository;

import lombok.RequiredArgsConstructor;
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
}
