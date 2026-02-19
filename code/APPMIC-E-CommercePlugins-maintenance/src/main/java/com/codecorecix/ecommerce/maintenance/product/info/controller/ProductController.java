package com.codecorecix.ecommerce.maintenance.product.info.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("${app.endpoints.product}")
public class ProductController {

  private final ProductService service;

  @GetMapping
  public ResponseEntity<GenericResponse<List<ProductInfo>>> getAllProducts() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getAllProducts());
  }

  @GetMapping("/active")
  public ResponseEntity<GenericResponse<List<ProductInfo>>> getAllActiveProducts() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getActiveProducts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> getProductById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/checkProducts")
  public ResponseEntity<GenericResponse<List<ProductInfo>>> checkProducts(@RequestParam final List<Integer> ids) {
    final GenericResponse<List<ProductInfo>> response = this.service.findByIds(ids);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<ProductResponseDto>> saveProduct(@RequestBody final ProductRequestDto productRequestDto) {
    MaintenanceUtils.validRequestDto(productRequestDto);
    if (ObjectUtils.isNotEmpty(productRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(productRequestDto));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> updateProduct(@PathVariable(value = "id") final Integer id,
    @RequestBody final ProductRequestDto productRequestDto) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      productRequestDto.setId(response.getBody().getId());
      MaintenanceUtils.validRequestDto(productRequestDto);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.save(productRequestDto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<GenericResponse<ProductResponseDto>> updateProductStatus(@PathVariable(value = "id") final Integer id,
    @RequestParam(value = "isActive") final Boolean isActive) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.updateProductStatus(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> deleteProductById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteProductById(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
