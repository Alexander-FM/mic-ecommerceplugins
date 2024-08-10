package com.codecorecix.ecommerce.maintenance.product.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductDetail;
import com.codecorecix.ecommerce.event.mapper.GenericFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductDetailResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDetailFieldsMapper extends GenericFieldsMapper<ProductDetailResponseDto, ProductDetail> {

  ProductDetailResponseDto destinationToSource(final ProductDetail destination);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", ignore = true)
  List<ProductDetailResponseDto> toDto(final List<ProductDetail> entityList);
}
