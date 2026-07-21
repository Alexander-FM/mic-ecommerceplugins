package com.codecorecix.ecommerce.maintenance.product.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.maintenance.product.image.mapper.ProductImageFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductAttributeFieldsMapper.class, ProductImageFieldsMapper.class})
public interface ProductFieldsMapper {

  Product sourceToDestination(final ProductRequestDto source);

  @Mapping(target = "categoryName", source = "category.description")
  @Mapping(target = "brandName", source = "brand.description")
  ProductResponseDto destinationToSource(final Product destination);

  //  @Mapping(target = "categoryName", source = "category.description")
  //  @Mapping(target = "brandName", source = "brand.description")
  List<ProductResponseDto> toDto(final List<Product> entityList);

  /**
   * Este método se utiliza para mapear un objeto de Product a un objeto de ProductInfo, que es un modelo más ligero utilizado para mostrar
   * información básica del producto en la interfaz de usuario. Aquí también mapeamos el nombre de la categoría y la marca directamente
   * desde las entidades relacionadas.
   *
   * @param products Objeto de Product que se va a mapear a ProductInfo.
   * @return Un objeto de ProductInfo con los campos mapeados, incluyendo el nombre de la categoría y la marca.
   */
  @Mapping(target = "categoryName", source = "category.description")
  @Mapping(target = "brandName", source = "brand.description")
  ProductInfo toProductListResponseDto(final Product products);

  // Mapeo de una lista de productos a una lista de ProductInfo a una lista de ProductInfo, que es un modelo más ligero utilizado para
  // mostrar información básica del producto en la interfaz de usuario.
  List<ProductInfo> toListDtoList(List<Product> products);

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