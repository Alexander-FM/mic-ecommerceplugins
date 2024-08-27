package com.codecorecix.ecommerce.maintenance.brand.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Brand;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.request.BrandRequestDto;
import com.codecorecix.ecommerce.maintenance.brand.api.dto.response.BrandResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandFieldsMapper {

  Brand sourceToDestination(final BrandRequestDto source);

  BrandResponseDto destinationToSource(final Brand destination);

  List<BrandResponseDto> toDto(final List<Brand> entityList);
}
