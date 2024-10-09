package com.codecorecix.ecommerce.maintenance.category.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface CategoryService {

  /**
   * Method used to list all categories.
   *
   * @return List of CategoryResponseDto.
   */
  GenericResponse<List<CategoryResponseDto>> listCategory();

  /**
   * Method used to list all active categories.
   *
   * @return List of CategoryResponseDto.
   */
  GenericResponse<List<CategoryResponseDto>> listActiveCategories();

  /**
   * Method used to save the category.
   *
   * @param categoryRequestDto The category request dto.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<CategoryResponseDto> saveCategory(final CategoryRequestDto categoryRequestDto);

  /**
   * Method used to delete the category.
   *
   * @param id The id of the category.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<CategoryResponseDto> deleteCategory(final Integer id);

  /**
   * Method used to desactive or activate the category.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<CategoryResponseDto> disabledOrEnabledCategory(final Boolean isActive, final Integer id);

  /**
   * Method used to find the category by id.
   *
   * @param id The id of the category.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<CategoryResponseDto> findById(final Integer id);
}
