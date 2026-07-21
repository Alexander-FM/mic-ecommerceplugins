package com.codecorecix.ecommerce.maintenance.category.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.maintenance.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.maintenance.category.service.CategoryService;
import com.codecorecix.ecommerce.maintenance.category.utils.CategoryConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.category}")
public class CategoryController {

  private final CategoryService service;

  public CategoryController(final CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<CategoryResponseDto>>> getAllCategories() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getAllCategories());
  }

  @GetMapping("/active")
  public ResponseEntity<GenericResponse<List<CategoryResponseDto>>> getActiveCategories() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getActiveCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> getCategoryById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<CategoryResponseDto>> createCategory(@RequestBody final CategoryRequestDto categoryRequestDto) {
    if (ObjectUtils.isNotEmpty(categoryRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(CategoryConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      MaintenanceUtils.validRequestDto(categoryRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(categoryRequestDto));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> updateCategory(@PathVariable(value = "id") final Integer id,
      @RequestBody final CategoryRequestDto categoryRequestDto) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      MaintenanceUtils.validRequestDto(categoryRequestDto);
      categoryRequestDto.setId(id);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.save(categoryRequestDto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> updateCategoryStatus(@PathVariable(value = "id") final Integer id,
      @RequestParam(value = "isActive") final Boolean isActive) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.updateCategoryStatus(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<CategoryResponseDto>> deleteCategoryById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CategoryResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteCategoryById(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

}
