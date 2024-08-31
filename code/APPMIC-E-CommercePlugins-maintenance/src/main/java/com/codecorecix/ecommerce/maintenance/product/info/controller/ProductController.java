package com.codecorecix.ecommerce.maintenance.product.info.controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.codecorecix.ecommerce.maintenance.product.detail.dto.response.ProductDetailResponseDto;
import com.codecorecix.ecommerce.maintenance.product.detail.repository.ProductDetailRepository;
import com.codecorecix.ecommerce.maintenance.product.detail.service.ProductDetailService;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;
import com.codecorecix.ecommerce.maintenance.product.image.repository.ProductImageRepository;
import com.codecorecix.ecommerce.maintenance.product.image.service.ProductImageService;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.info.service.ProductService;
import com.codecorecix.ecommerce.maintenance.product.info.utils.ProductConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUnprocessableEntityException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("api/product")
public class ProductController {

  private final ProductService service;

  private final ProductImageRepository productImageRepository;

  private final ProductImageService productImageService;

  private final ProductDetailRepository productDetailRepository;

  private final ProductDetailService productDetailService;

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
  public ResponseEntity<GenericResponse<ProductResponseDto>> saveProduct(@RequestBody final ProductRequestDto productRequestDto) {
    this.validRequestDto(productRequestDto);
    if (ObjectUtils.isNotEmpty(productRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(ProductConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveProduct(productRequestDto));
    }
  }

  @PutMapping("/updateProduct/{id}")
  public ResponseEntity<GenericResponse<ProductResponseDto>> updateProduct(@PathVariable(value = "id") final Integer id,
      @RequestBody final ProductRequestDto productRequestDto) {
    final GenericResponse<ProductResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      productRequestDto.setId(response.getBody().getId());
      this.validRequestDto(productRequestDto);
      this.deleteImagesAndDetailsOfProduct(response.getBody().getImages(), response.getBody().getDetails());
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

  /**
   * Méthod for valid the product request dto.
   *
   * @param productRequestDto the product request dto.
   */
  private void validRequestDto(final ProductRequestDto productRequestDto) {
    final Validator validator;
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
    final Set<ConstraintViolation<Object>> violations = validator.validate(productRequestDto);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  /**
   * Method for delete details and images of products.
   *
   * @param productImageList the image product list.
   * @param productDetailList the detail product list.
   */
  private void deleteImagesAndDetailsOfProduct(List<ProductImageResponseDto> productImageList,
      List<ProductDetailResponseDto> productDetailList) {
    if (Objects.nonNull(productImageList) && Objects.nonNull(productDetailList)) {
      productImageList.forEach(productImageDto -> this.productImageService.deleteImage(productImageDto.getId()));
      productDetailList.forEach(productDetailDto -> this.productDetailService.deleteDetail(productDetailDto.getId()));
    }
  }
}
