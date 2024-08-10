package com.codecorecix.ecommerce.maintenance.product.image.mapper;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.event.mapper.GenericFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageFieldsMapper extends GenericFieldsMapper<ProductResponseDto, ProductImage> {

  ProductImage sourceToDestination(final ProductImageRequestDto source);

  ProductImageResponseDto destinationToSource(final ProductImage destination);

}
