package com.codecorecix.ecommerce.maintenance.product.detail.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.product.detail.dto.request.ProductDetailRequestDto;
import com.codecorecix.ecommerce.maintenance.product.detail.dto.response.ProductDetailResponseDto;

public interface ProductDetailService {

  /**
   * Saves a new product detail.
   *
   * @param productDetailRequestDto the object containing the information of the detail to be saved.
   * @return a {@code ProductImageResponseDto} containing the information of the saved detail.
   */
  ProductDetailResponseDto saveDetail(final ProductDetailRequestDto productDetailRequestDto);

  /**
   * Deletes a product detail by its ID.
   *
   * @param id the identifier of the detail to be deleted.
   */
  void deleteDetail(final Integer id);

  /**
   * Finds a product detail by its ID.
   *
   * @param id the identifier of the detail to be found.
   * @return a {@code ProductImageResponseDto} containing the information of the found detail.
   */
  ProductDetailResponseDto findById(final Integer id);

  /**
   * Finds a list product detail by its product id.
   *
   * @param productId the identifier of the detail to be found.
   * @return a {@code ProductImageResponseDto} containing the information of the found detail.
   */
  List<ProductDetailResponseDto> findByProductId(final Integer productId);
}
