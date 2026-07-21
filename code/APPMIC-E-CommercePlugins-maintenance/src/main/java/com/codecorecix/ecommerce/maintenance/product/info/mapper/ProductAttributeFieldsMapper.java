package com.codecorecix.ecommerce.maintenance.product.info.mapper;

import com.codecorecix.ecommerce.event.entities.ProductAttribute;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductAttributeRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductAttributeResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeFieldsMapper {
  // Mapeamos el ID del DTO al ID de la entidad anidada
  @Mapping(target = "attribute.id", source = "attributeId")
  ProductAttribute toEntity(ProductAttributeRequestDto source);

  // Mapeo inverso para la respuesta
  @Mapping(target = "attributeId", source = "attribute.id")
  @Mapping(target = "name", source = "attribute.name")
  ProductAttributeResponseDto toResponseDto(ProductAttribute entity);
}
