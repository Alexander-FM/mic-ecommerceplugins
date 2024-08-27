package com.codecorecix.ecommerce.maintenance.product.detail.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductDetail;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.request.ProductDetailRequestDto;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.response.ProductDetailResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDetailFieldsMapper {

  @Mapping(target = "product.id", source = "productId")
  ProductDetail sourceToDestination(final ProductDetailRequestDto source);

  @Mapping(target = "productId", source = "product.id")
  ProductDetailResponseDto destinationToSource(final ProductDetail destination);

  @Mapping(target = "productId", source = "product.id")
  List<ProductDetailResponseDto> toDto(List<ProductDetail> entityList);
}
