package com.codecorecix.ecommerce.maintenance.product.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.maintenance.product.api.dto.request.ProductRequestDto;
import com.codecorecix.ecommerce.maintenance.product.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.maintenance.product.mapper.ProductFieldsMapper;
import com.codecorecix.ecommerce.maintenance.product.repository.ProductRepository;
import com.codecorecix.ecommerce.maintenance.product.utils.ProductConstants;
import com.codecorecix.ecommerce.maintenance.product.utils.ProductUtils;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;

  private final ProductFieldsMapper mapper;

  public ProductServiceImpl(ProductRepository repository, ProductFieldsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public GenericResponse<List<ProductResponseDto>> listProduct() {
    return new GenericResponse<>(GenericResponseConstants.TIPO_RESULT, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA, this.mapper.toDto(this.repository.findAll()));
  }

  @Override
  public GenericResponse<List<ProductResponseDto>> listActiveProducts() {
    return new GenericResponse<>(GenericResponseConstants.TIPO_RESULT, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA, this.mapper.toDto(this.repository.findByIsActiveIsTrue()));
  }

  @Override
  public GenericResponse<ProductResponseDto> saveProduct(final ProductRequestDto productRequestDto) {
    final Product product = (this.repository.save(this.mapper.sourceToDestination(productRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
        GenericResponseConstants.OPERACION_CORRECTA, this.mapper.destinationToSource(product));
  }

  @Override
  public GenericResponse<ProductResponseDto> deleteProduct(final Integer id) {
    final Optional<Product> product = this.repository.findById(id);
    if (product.isPresent()) {
      this.repository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
          GenericResponseConstants.OPERACION_CORRECTA, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_INCORRECTA, ProductConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<ProductResponseDto> desactivateOrActivateProduct(final Boolean isActive, final Integer id) {
    final Optional<Product> product = this.repository.findById(id);
    if (product.isPresent()) {
      this.repository.desactivateOrActivateProduct(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_OK,
          GenericResponseConstants.OPERACION_CORRECTA, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.TIPO_DATA, GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.OPERACION_INCORRECTA, ProductConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<ProductResponseDto> findById(final Integer id) {
    final Optional<Product> product = this.repository.findById(id);
    return product.map(value -> ProductUtils.buildGenericResponseSuccess(this.mapper.destinationToSource(value)))
        .orElseGet(ProductUtils::buildGenericResponseError);
  }
}
