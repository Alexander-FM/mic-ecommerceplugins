package com.codecorecix.ecommerce.maintenance.role.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Role;
import com.codecorecix.ecommerce.maintenance.role.dto.request.RoleRequestDto;
import com.codecorecix.ecommerce.maintenance.role.dto.response.RoleResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleFieldsMapper {

  Role sourceToDestination(final RoleRequestDto source);

  RoleResponseDto destinationToSource(final Role destination);

  List<RoleResponseDto> toDto(final List<Role> entityList);
}
