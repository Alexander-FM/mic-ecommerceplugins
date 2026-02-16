package com.codecorecix.ecommerce.maintenance.product.detail.controller;

import java.util.List;

import com.codecorecix.ecommerce.exception.GenericException;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.request.ProductDetailRequestDto;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.response.ProductDetailResponseDto;
import com.codecorecix.ecommerce.maintenance.product.detail.service.ProductDetailService;
import com.codecorecix.ecommerce.utils.GenericErrorMessage;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import jakarta.validation.ConstraintViolationException;
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
@RequestMapping("${app.endpoints.product-detail}")
@RequiredArgsConstructor
public class ProductDetailController {

  private final ProductDetailService productDetailService;

  @GetMapping("/{productId}")
  public ResponseEntity<GenericResponse<List<ProductDetailResponseDto>>> getDetailByProductId(
      @PathVariable(name = "productId") final Integer productId) {
    try {
      List<ProductDetailResponseDto> productDetailResponseDto = this.productDetailService.getDetailByProductId(productId);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, productDetailResponseDto));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_SAVE_ERROR);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> saveDetail(@RequestBody ProductDetailRequestDto requestDto) {
    try {
      MaintenanceUtils.validRequestDto(requestDto);
      ProductDetailResponseDto productDetailResponseDto = this.productDetailService.saveDetail(requestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(
          GenericUtils.buildGenericResponseSuccess(null, productDetailResponseDto));
    } catch (final ConstraintViolationException e) {
      throw new ConstraintViolationException(e.getConstraintViolations());
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_SAVE_ERROR);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> updateDetail(@PathVariable(value = "id") final Integer id,
      @RequestBody final ProductDetailRequestDto productDetailRequestDto) {
    final ProductDetailResponseDto response = this.productDetailService.findById(id);
    if (ObjectUtils.isNotEmpty(response)) {
      productDetailRequestDto.setId(id);
      MaintenanceUtils.validRequestDto(productDetailRequestDto);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, this.productDetailService.saveDetail(productDetailRequestDto)));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          GenericUtils.buildGenericResponseError(null, null));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> deleteDetail(@PathVariable(name = "id") final Integer id) {
    try {
      this.productDetailService.deleteDetail(id);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, new ProductDetailResponseDto()));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_DELETE_ERROR);
    }
  }
}
