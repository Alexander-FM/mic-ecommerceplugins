package com.codecorecix.ecommerce.maintenance.product.info.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.maintenance.product.info.utils.ProductConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUnprocessableEntityException;

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
@RequestMapping("api/product")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/listProduct")
  public ResponseEntity<GenericResponse<List<ProductResponseDto>>> listProduct() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listProduct());
  }

  @GetMapping("/listActiveProduct")
  public ResponseEntity<GenericResponse<List<ProductResponseDto>>> listActiveProduct() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listActiveProducts());
  }

  @GetMapping("/getById/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> getProductById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping("/saveProduct")
  public ResponseEntity<GenericResponse<ProductResponseDto>> saveProduct(
      @Valid @RequestBody final ProductRequestDto productRequestDto) {
    if (ObjectUtils.isNotEmpty(productRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(ProductConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveProduct(productRequestDto));
    }
  }

  @PutMapping("/updateProduct/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> updateProduct(@Valid @PathVariable(value = "id") final Integer id,
      @RequestBody final ProductRequestDto productRequestDto) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      productRequestDto.setId(id);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveProduct(productRequestDto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/desactivateOrActivateProduct/{id}/{isActive}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> desactivateOrActivateProduct(@PathVariable(value = "id") final Integer id,
      @PathVariable(value = "isActive") final Boolean isActive) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.desactivateOrActivateProduct(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/deleteProduct/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> deleteProductById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteProduct(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

}
