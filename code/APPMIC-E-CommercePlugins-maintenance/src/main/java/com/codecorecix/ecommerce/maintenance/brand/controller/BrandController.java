package com.codecorecix.ecommerce.maintenance.brand.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.request.BrandRequestDto;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.response.BrandResponseDto;
import com.codecorecix.ecommerce.maintenance.brand.service.BrandService;
import com.codecorecix.ecommerce.maintenance.brand.utils.BrandConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
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
@RequestMapping("api/brand")
public class BrandController {

  private final BrandService service;

  public BrandController(final BrandService service) {
    this.service = service;
  }

  @GetMapping("/listBrand")
  public ResponseEntity<GenericResponse<List<BrandResponseDto>>> listBrand() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listBrand());
  }

  @GetMapping("/listActiveBrand")
  public ResponseEntity<GenericResponse<List<BrandResponseDto>>> listActiveBrand() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listActiveBrands());
  }

  @GetMapping("/getById/{id}")
  public ResponseEntity<GenericResponse<BrandResponseDto>> getBrandById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<BrandResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping("/saveBrand")
  public ResponseEntity<GenericResponse<BrandResponseDto>> saveBrand(
      @Valid @RequestBody final BrandRequestDto categoryRequestDto) {
    if (ObjectUtils.isNotEmpty(categoryRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(BrandConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveBrand(categoryRequestDto));
    }
  }

  @PutMapping("/updateBrand/{id}")
  public ResponseEntity<GenericResponse<BrandResponseDto>> updateBrand(@Valid @PathVariable(value = "id") final Integer id,
      @RequestBody final BrandRequestDto categoryRequestDto) {
    final GenericResponse<BrandResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      categoryRequestDto.setId(id);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveBrand(categoryRequestDto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/disabledOrEnabledBrand/{id}/{isActive}")
  public ResponseEntity<GenericResponse<BrandResponseDto>> disabledOrEnabledBrand(@PathVariable(value = "id") final Integer id,
      @PathVariable(value = "isActive") final Boolean isActive) {
    final GenericResponse<BrandResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.disabledOrEnabledBrand(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/deleteBrand/{id}")
  public ResponseEntity<GenericResponse<BrandResponseDto>> deleteBrandById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<BrandResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteBrand(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
