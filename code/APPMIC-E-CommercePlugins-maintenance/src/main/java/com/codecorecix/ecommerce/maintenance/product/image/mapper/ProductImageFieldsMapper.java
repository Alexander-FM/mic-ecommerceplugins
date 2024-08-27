package com.codecorecix.ecommerce.maintenance.product.image.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageFieldsMapper {

  @Mapping(target = "product.id", source = "productId")
  ProductImage sourceToDestination(final ProductImageRequestDto source);

  ProductImageResponseDto destinationToSource(final ProductImage destination);

  List<ProductImageResponseDto> toDto(List<ProductImage> entityList);
}
