package com.codecorecix.ecommerce.maintenance.attribute.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Attribute;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.request.AttributeRequestDto;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.response.AttributeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttributeFieldsMapper {

  Attribute toEntity(final AttributeRequestDto source);

  AttributeResponseDto toDto(final Attribute entity);

  List<AttributeResponseDto> toDto(final List<Attribute> entities);
}

