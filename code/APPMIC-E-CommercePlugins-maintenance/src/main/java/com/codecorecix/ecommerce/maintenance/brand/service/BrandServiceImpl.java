package com.codecorecix.ecommerce.maintenance.brand.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Brand;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.request.BrandRequestDto;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.response.BrandResponseDto;
import com.codecorecix.ecommerce.maintenance.brand.mapper.BrandFieldsMapper;
import com.codecorecix.ecommerce.maintenance.brand.repository.BrandRepository;
import com.codecorecix.ecommerce.maintenance.brand.utils.BrandConstants;
import com.codecorecix.ecommerce.maintenance.brand.utils.BrandUtils;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

  private final BrandFieldsMapper mapper;

  private final BrandRepository repository;

  @Override
  public GenericResponse<List<BrandResponseDto>> listBrand() {
    final List<Brand> brands = this.repository.findAll();
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, this.mapper.toDto(brands));
  }

  @Override
  public GenericResponse<List<BrandResponseDto>> listActiveBrands() {
    final List<Brand> brands = this.repository.findByIsActiveIsTrue();
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, this.mapper.toDto(brands));
  }

  @Override
  public GenericResponse<BrandResponseDto> saveBrand(final BrandRequestDto brandRequestDto) {
    final Brand brand = (this.repository.save(this.mapper.sourceToDestination(brandRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.destinationToSource(brand));
  }

  @Override
  public GenericResponse<BrandResponseDto> deleteBrand(final Integer id) {
    final Optional<Brand> brand = this.repository.findById(id);
    if (brand.isPresent()) {
      this.repository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, BrandConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<BrandResponseDto> disabledOrEnabledBrand(final Boolean isActive, final Integer id) {
    final Optional<Brand> brandOptional = this.repository.findById(id);
    if (brandOptional.isPresent()) {
      this.repository.disabledOrEnabledBrand(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, BrandConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<BrandResponseDto> findById(final Integer id) {
    final Optional<Brand> brand = this.repository.findById(id);
    return brand.map(value -> BrandUtils.buildGenericResponseSuccess(this.mapper.destinationToSource(value)))
        .orElseGet(BrandUtils::buildGenericResponseError);
  }
}
