package com.codecorecix.ecommerce.maintenance.category.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.maintenance.category.service.CategoryService;
import com.codecorecix.ecommerce.maintenance.category.utils.CategoryConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;

import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/category")
public class CategoryController {

  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping("/listCategory")
  public ResponseEntity<GenericResponse<List<CategoryResponseDto>>> listCategory() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listCategory());
  }

  @GetMapping("/listActiveCategory")
  public ResponseEntity<GenericResponse<List<CategoryResponseDto>>> listActiveCategory() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listActiveCategories());
  }

  @GetMapping("/getById/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> getCategoryById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping("/saveCategory")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> saveCategory(
      @Valid @RequestBody final CategoryRequestDto categoryRequestDto) {
    if (ObjectUtils.isNotEmpty(categoryRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(CategoryConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveCategory(categoryRequestDto));
    }
  }

  @PutMapping("/updateCategory/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> updateCategory(@Valid @PathVariable(value = "id") final Integer id,
      @RequestBody final CategoryRequestDto categoryRequestDto) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      categoryRequestDto.setId(id);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveCategory(categoryRequestDto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/disabledOrEnabledCategory/{id}/{isActive}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> disabledOrEnabledCategory(@PathVariable(value = "id") final Integer id,
      @PathVariable(value = "isActive") final Boolean isActive) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.disabledOrEnabledCategory(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/deleteCategory/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> deleteCategoryById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteCategory(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

}
