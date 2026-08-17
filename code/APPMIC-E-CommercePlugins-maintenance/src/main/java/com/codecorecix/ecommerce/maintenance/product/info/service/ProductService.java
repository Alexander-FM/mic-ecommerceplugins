package com.codecorecix.ecommerce.maintenance.product.info.service;

import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import java.util.List;

public interface ProductService {

  /**
   * Method used to list all products.
   *
   * @return List of ProductResponseDto.
   */
  GenericResponse<List<ProductInfo>> getAllProducts();

  /**
   * Method used to list all active products.
   *
   * @return List of ProductResponseDto.
   */
  GenericResponse<List<ProductInfo>> getActiveProducts();

  /**
   * Method used to save the product.
   *
   * @param productRequestDto The category request dto.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<ProductResponseDto> save(final ProductRequestDto productRequestDto);

  /**
   * Method used to delete the product.
   *
   * @param id The id of the product.
   * @return List of ProductResponseDto.
   */
  GenericResponse<ProductResponseDto> deleteProductById(final Integer id);

  /**
   * Method used to update status of the product.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of ProductResponseDto.
   */
  GenericResponse<ProductResponseDto> updateProductStatus(final Boolean isActive, final Integer id);

  /**
   * Method used to find the product by id.
   *
   * @param id The id of the product.
   * @return List of ProductResponseDto.
   */
  GenericResponse<ProductResponseDto> findById(final Integer id);

  /**
   * Method used to list all products by ids.
   *
   * @return List of ProductInfo.
   */
  GenericResponse<List<ProductInfo>> findByIds(final List<Integer> ids);

  /**
   * Method used to list all products by category id.
   *
   * @param categoryId The id of the category.
   * @return List of ProductInfo.
   */
  GenericResponse<List<ProductInfo>> findByCategoryId(final Integer categoryId);
}