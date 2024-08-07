package com.codecorecix.ecommerce.maintenance.category.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.event.mapper.GenericFieldsMapper;
import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryFieldsMapper extends GenericFieldsMapper<CategoryResponseDto, Category> {

  Category sourceToDestination(final CategoryRequestDto source);

  CategoryResponseDto destinationToSource(final Category destination);

  @Override
  List<CategoryResponseDto> toDto(final List<Category> entityList);
}
