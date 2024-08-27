package com.codecorecix.ecommerce.maintenance.product.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductFieldsMapper {

  Product sourceToDestination(final ProductRequestDto source);

  @Mapping(target = "categoryName", source = "destination.category.description")
  @Mapping(target = "brandName", source = "destination.brand.description")
  ProductResponseDto destinationToSource(final Product destination);

  @Mapping(target = "categoryName", source = "entityList.category.description")
  @Mapping(target = "brandName", source = "entityList.brand.description")
  List<ProductResponseDto> toDto(final List<Product> entityList);
}
