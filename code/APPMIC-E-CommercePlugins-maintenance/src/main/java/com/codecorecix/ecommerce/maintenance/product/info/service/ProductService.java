package com.codecorecix.ecommerce.maintenance.product.info.service;

import java.util.List;

import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface ProductService {

  /**
   * Method used to list all products.
   *
   * @return List of ProductResponseDto.
   */
  GenericResponse<List<ProductResponseDto>> listProduct();

  /**
   * Method used to list all active products.
   *
   * @return List of ProductResponseDto.
   */
  GenericResponse<List<ProductResponseDto>> listActiveProducts();

  /**
   * Method used to save the product.
   *
   * @param productRequestDto The category request dto.
   * @return List of CategoryResponseDto.
   */
  GenericResponse<ProductResponseDto> saveProduct(final ProductRequestDto productRequestDto);

  /**
   * Method used to delete the product.
   *
   * @param id The id of the product.
   * @return List of ProductResponseDto.
   */
  GenericResponse<ProductResponseDto> deleteProduct(final Integer id);

  /**
   * Method used to desactive or activate the product.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of ProductResponseDto.
   */
  GenericResponse<ProductResponseDto> disabledOrEnabledProduct(final Boolean isActive, final Integer id);

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
}
