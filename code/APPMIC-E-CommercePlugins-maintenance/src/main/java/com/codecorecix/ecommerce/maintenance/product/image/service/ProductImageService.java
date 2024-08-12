package com.codecorecix.ecommerce.maintenance.product.image.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.product.image.api.dto.request.ProductImageRequestDto;
import com.codecorecix.ecommerce.maintenance.product.image.api.dto.response.ProductImageResponseDto;

public interface ProductImageService {

  /**
   * Saves a new product image.
   *
   * @param productImageRequestDto the object containing the information of the image to be saved.
   * @return a {@code ProductImageResponseDto} containing the information of the saved image.
   */
  ProductImageResponseDto saveImage(final ProductImageRequestDto productImageRequestDto);

  /**
   * Deletes a product image by its ID.
   *
   * @param id the identifier of the image to be deleted.
   * @return a {@code ProductImageResponseDto} containing the information of the deleted image.
   */
  ProductImageResponseDto deleteImage(final Integer id);

  /**
   * Finds a product image by its ID.
   *
   * @param id the identifier of the image to be found.
   * @return a {@code ProductImageResponseDto} containing the information of the found image.
   */
  ProductImageResponseDto findById(final Integer id);

  /**
   * Finds a product image by its url.
   *
   * @param urlName the identifier of the image to be found.
   * @return a {@code ProductImageResponseDto} containing the information of the found image.
   */
  ProductImageResponseDto findByUrlName(final String urlName);

  /**
   * Finds a list product image by its product id.
   *
   * @param productId the identifier of the image to be found.
   * @return a {@code ProductImageResponseDto} containing the information of the found image.
   */
  List<ProductImageResponseDto> findByUrlProductId(final Integer productId);
}
