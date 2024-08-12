package com.codecorecix.ecommerce.maintenance.product.image.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.event.mapper.GenericFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageFieldsMapper extends GenericFieldsMapper<ProductImageResponseDto, ProductImage> {

  ProductImage sourceToDestination(final ProductImageRequestDto source);

  ProductImageResponseDto destinationToSource(final ProductImage destination);

  @Override
  List<ProductImageResponseDto> toDto(List<ProductImage> entityList);
}
