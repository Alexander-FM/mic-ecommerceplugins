package com.codecorecix.ecommerce.maintenance.category.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Category;
import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.maintenance.category.mapper.CategoryFieldsMapper;
import com.codecorecix.ecommerce.maintenance.category.repository.CategoryRepository;
import com.codecorecix.ecommerce.maintenance.category.utils.CategoryConstants;
import com.codecorecix.ecommerce.maintenance.category.utils.CategoryUtils;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repository;

  private final CategoryFieldsMapper mapper;

  public CategoryServiceImpl(CategoryRepository repository, CategoryFieldsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public GenericResponse<List<CategoryResponseDto>> listCategory() {
    final List<Category> categories = this.getHierarchicalList(this.repository.findCategoryByParentCategoryIsNull());
    return new GenericResponse<>(GenericResponseConstants.TIPO_RESULT, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA,
        this.mapper.toDto(categories));
  }

  @Override
  public GenericResponse<List<CategoryResponseDto>> listActiveCategories() {
    return new GenericResponse<>(GenericResponseConstants.TIPO_RESULT, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA, this.mapper.toDto(this.repository.findByIsActiveIsTrue()));
  }

  @Override
  public GenericResponse<CategoryResponseDto> saveCategory(final CategoryRequestDto categoryRequestDto) {
    final Category category = (this.repository.save(this.mapper.sourceToDestination(categoryRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA, this.mapper.destinationToSource(category));
  }

  @Override
  public GenericResponse<CategoryResponseDto> deleteCategory(final Integer id) {
    final Optional<Category> category = this.repository.findById(id);
    if (category.isPresent()) {
      this.repository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
          GenericResponseConstants.OPERACION_CORRECTA, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_INCORRECTA, CategoryConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<CategoryResponseDto> desactivateOrActivateCategory(final Boolean isActive, final Integer id) {
    final Optional<Category> categoryOptional = this.repository.findById(id);
    if (categoryOptional.isPresent()) {
      this.repository.desactivateOrActivateCategory(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
          GenericResponseConstants.OPERACION_CORRECTA, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_INCORRECTA, CategoryConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<CategoryResponseDto> findById(final Integer id) {
    final Optional<Category> category = this.repository.findById(id);
    return category.map(value -> CategoryUtils.buildGenericResponseSuccess(this.mapper.destinationToSource(value)))
        .orElseGet(CategoryUtils::buildGenericResponseError);
  }

  private List<Category> getHierarchicalList(final List<Category> categories) {
    for (final Category category : categories) {
      category.setId(category.getId());
      category.setDescription(category.getDescription());
      category.setIsActive(category.getIsActive());
      category.setSubCategories(this.repository.findCategoryByParentCategory(category.getId()));
      this.getHierarchicalList(category.getSubCategories());
    }
    return categories;
  }
}
