package com.codecorecix.ecommerce.category.api.mapper;

import java.util.List;

import com.codecorecix.ecommerce.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.event.mapper.GenericFieldsMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaFieldsMapper extends GenericFieldsMapper<CategoryResponseDto, Category> {

  Category sourceToDestination(final CategoryRequestDto source);

  CategoryResponseDto destinationToSource(final Category destination);

  @Override
  List<CategoryResponseDto> toDto(final List<Category> entityList);
}
