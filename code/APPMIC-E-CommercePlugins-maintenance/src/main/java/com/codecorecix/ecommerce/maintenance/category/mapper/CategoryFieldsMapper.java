package com.codecorecix.ecommerce.maintenance.category.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryFieldsMapper {

  @Mapping(target = "subCategories", ignore = true)
  @Mapping(target = "parentCategory", source = "parentCategory")
  Category sourceToDestination(final CategoryRequestDto source);

  @Mapping(target = "subCategories", source = "subCategories")
  CategoryResponseDto destinationToSource(final Category destination);

  List<CategoryResponseDto> toDto(final List<Category> entityList);
}
