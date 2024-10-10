package com.codecorecix.ecommerce.maintenance.product.detail.controller;

import java.util.List;

import com.codecorecix.ecommerce.exception.GenericException;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.request.ProductDetailRequestDto;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.response.ProductDetailResponseDto;
import com.codecorecix.ecommerce.maintenance.product.detail.service.ProductDetailService;
import com.codecorecix.ecommerce.utils.GenericErrorMessage;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;
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
@RequestMapping("api/productDetail")
@RequiredArgsConstructor
public class ProductDetailController {

  private final ProductDetailService productDetailService;

  @GetMapping("/listDetailByProductId/{productId}")
  public ResponseEntity<GenericResponse<List<ProductDetailResponseDto>>> saveDetail(
      @PathVariable(name = "productId") final Integer productId) {
    try {
      List<ProductDetailResponseDto> productDetailResponseDto = this.productDetailService.findByProductId(productId);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, productDetailResponseDto));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_SAVE_ERROR);
    }
  }

  @PostMapping("/saveDetail")
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> saveDetail(@RequestBody ProductDetailRequestDto requestDto) {
    try {
      ProductDetailResponseDto productDetailResponseDto = this.productDetailService.saveDetail(requestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(
          GenericUtils.buildGenericResponseSuccess(null, productDetailResponseDto));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_SAVE_ERROR);
    }
  }

  @PutMapping("/updateDetail/{id}")
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> updateDetail(@PathVariable(value = "id") final Integer id,
      @RequestBody final ProductDetailRequestDto productDetailRequestDto) {
    final ProductDetailResponseDto response = this.productDetailService.findById(id);
    if (ObjectUtils.isNotEmpty(response)) {
      productDetailRequestDto.setId(id);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, this.productDetailService.saveDetail(productDetailRequestDto)));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          GenericUtils.buildGenericResponseError(null, null));
    }
  }

  @DeleteMapping("/deleteDetailById/{id}")
  public ResponseEntity<GenericResponse<ProductDetailResponseDto>> deleteImage(@PathVariable(name = "id") final Integer id) {
    try {
      this.productDetailService.deleteDetail(id);
      return ResponseEntity.status(HttpStatus.OK).body(
          GenericUtils.buildGenericResponseSuccess(null, new ProductDetailResponseDto()));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_DELETE_ERROR);
    }
  }
}
