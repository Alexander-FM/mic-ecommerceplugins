package com.codecorecix.ecommerce.maintenance.product.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductFieldsMapper {

  Product sourceToDestination(final ProductRequestDto source);

  @Mapping(target = "categoryName", source = "destination.category.description")
  @Mapping(target = "brandName", source = "destination.brand.description")
  ProductResponseDto destinationToSource(final Product destination);

  @Mapping(target = "categoryName", source = "entityList.category.description")
  @Mapping(target = "brandName", source = "entityList.brand.description")
  @Mapping(target = "details", source = "entityList.details")
  List<ProductResponseDto> toDto(final List<Product> entityList);

  @AfterMapping
  default void afterDestinationToSource(@MappingTarget ProductResponseDto productResponseDto, Product product) {
    if (product.getDetails() != null) {
      productResponseDto.getDetails().forEach(detail -> detail.setProductId(product.getId()));
    }
  }

  @AfterMapping
  default void afterToDto(@MappingTarget List<ProductResponseDto> productResponseDtos, List<Product> products) {
    for (int i = 0; i < products.size(); i++) {
      Product product = products.get(i);
      ProductResponseDto productResponseDto = productResponseDtos.get(i);
      if (product.getDetails() != null) {
        productResponseDto.getDetails().forEach(detail -> detail.setProductId(product.getId()));
      }
    }
  }
}
