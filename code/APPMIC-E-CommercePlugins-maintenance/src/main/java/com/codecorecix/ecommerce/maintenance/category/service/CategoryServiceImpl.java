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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repository;

  private final CategoryFieldsMapper mapper;

  @Override
  public GenericResponse<List<CategoryResponseDto>> listCategory() {
    final List<Category> categories = this.getHierarchicalList(this.repository.findCategoryByParentCategoryIsNull(), true);
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.toDto(categories));
  }

  @Override
  public GenericResponse<List<CategoryResponseDto>> listActiveCategories() {
    final List<Category> categories =
        this.getHierarchicalList(this.repository.findCategoryByParentCategoryIsNullAndIsActiveIsTrue(), false);
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.toDto(categories));
  }

  @Override
  public GenericResponse<CategoryResponseDto> saveCategory(final CategoryRequestDto categoryRequestDto) {
    final Category category = (this.repository.save(this.mapper.sourceToDestination(categoryRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.destinationToSource(category));
  }

  @Override
  public GenericResponse<CategoryResponseDto> deleteCategory(final Integer id) {
    final Optional<Category> category = this.repository.findById(id);
    if (category.isPresent()) {
      this.repository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, CategoryConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<CategoryResponseDto> disabledOrEnabledCategory(final Boolean isActive, final Integer id) {
    final Optional<Category> categoryOptional = this.repository.findById(id);
    if (categoryOptional.isPresent()) {
      this.repository.disabledOrEnabledCategory(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, CategoryConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<CategoryResponseDto> findById(final Integer id) {
    final Optional<Category> category = this.repository.findById(id);
    return category.map(value -> CategoryUtils.buildGenericResponseSuccess(this.mapper.destinationToSource(value)))
        .orElseGet(CategoryUtils::buildGenericResponseSuccessError);
  }

  private List<Category> getHierarchicalList(final List<Category> categories, final boolean isActive) {
    for (final Category category : categories) {
      category.setId(category.getId());
      category.setDescription(category.getDescription());
      category.setIsActive(category.getIsActive());
      category.setSubCategories(isActive ? this.repository.findCategoryByParentCategory(category.getId())
          : this.repository.findCategoryByParentCategoryAndIsActiveIsTrue(category.getId()));
      this.getHierarchicalList(category.getSubCategories(), isActive);
    }
    return categories;
  }
}
