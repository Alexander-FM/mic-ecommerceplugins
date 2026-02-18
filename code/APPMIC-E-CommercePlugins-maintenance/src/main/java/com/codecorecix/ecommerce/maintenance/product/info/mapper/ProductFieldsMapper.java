package com.codecorecix.ecommerce.maintenance.product.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductAttributeFieldsMapper.class})
public interface ProductFieldsMapper {

  Product sourceToDestination(final ProductRequestDto source);

  @Mapping(target = "categoryName", source = "category.description")
  @Mapping(target = "brandName", source = "brand.description")
  ProductResponseDto destinationToSource(final Product destination);

  @Mapping(target = "categoryName", source = "category.description")
  @Mapping(target = "brandName", source = "brand.description")
  List<ProductResponseDto> toDto(final List<Product> entityList);

  /**
   * Este metodo se ejecuta AUTOMÁTICAMENTE después de que MapStruct termina el mapeo. Aquí vinculamos cada atributo con el producto padre.
   */
  @AfterMapping
  default void linkAttributes(@MappingTarget Product product) {
    if (product.getAttributes() != null) {
      product.getAttributes().forEach(attribute -> attribute.setProduct(product));
    }
    if (product.getImages() != null) {
      product.getImages().forEach(image -> image.setProduct(product));
    }
  }
}