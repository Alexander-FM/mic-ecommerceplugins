package com.codecorecix.ecommerce.category.api.service;

import java.util.List;

import com.codecorecix.ecommerce.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.category.api.dto.response.CategoryResponseDto;

public interface CategoriaService {

  /**
   * Method used to list categories.
   *
   * @param categoryRequestDto The category request dto.
   * @return List of CategoryResponseDto.
   */
  List<CategoryResponseDto> categoryList(final CategoryRequestDto categoryRequestDto);
}
