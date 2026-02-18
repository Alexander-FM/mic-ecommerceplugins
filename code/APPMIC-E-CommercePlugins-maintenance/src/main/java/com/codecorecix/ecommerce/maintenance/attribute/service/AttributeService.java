package com.codecorecix.ecommerce.maintenance.attribute.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.attribute.api.dto.request.AttributeRequestDto;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.response.AttributeResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface AttributeService {

  GenericResponse<List<AttributeResponseDto>> getAllAttributes();

  GenericResponse<AttributeResponseDto> findById(Integer id);

  GenericResponse<AttributeResponseDto> save(AttributeRequestDto dto);

  GenericResponse<AttributeResponseDto> update(Integer id, AttributeRequestDto dto);

  GenericResponse<AttributeResponseDto> deleteById(Integer id);
}

