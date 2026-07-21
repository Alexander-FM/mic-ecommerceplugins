package com.codecorecix.ecommerce.maintenance.role.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Role;
import com.codecorecix.ecommerce.maintenance.role.dto.request.RoleRequestDto;
import com.codecorecix.ecommerce.maintenance.role.dto.response.RoleResponseDto;
import com.codecorecix.ecommerce.maintenance.role.mapper.RoleFieldsMapper;
import com.codecorecix.ecommerce.maintenance.role.repository.RoleRepository;
import com.codecorecix.ecommerce.maintenance.role.utils.RoleConstants;
import com.codecorecix.ecommerce.maintenance.role.utils.RoleUtils;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleFieldsMapper mapper;

  private final RoleRepository repository;

  @Override
  public GenericResponse<List<RoleResponseDto>> getAllRoles() {
    final List<Role> roles = this.repository.findAll();
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, this.mapper.toDto(roles));
  }

  @Override
  public GenericResponse<RoleResponseDto> save(final RoleRequestDto roleRequestDto) {
    final Role role = (this.repository.save(this.mapper.sourceToDestination(roleRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.destinationToSource(role));
  }

  @Override
  public GenericResponse<RoleResponseDto> deleteById(final Integer id) {
    final Optional<Role> rol = this.repository.findById(id);
    if (rol.isPresent()) {
      this.repository.deleteById(id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, RoleConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<RoleResponseDto> updateBrandStatus(final Boolean isActive, final Integer id) {
    final Optional<Role> roleOptional = this.repository.findById(id);
    if (roleOptional.isPresent()) {
      this.repository.disabledOrEnabledBrand(isActive, id);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, RoleConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<RoleResponseDto> findById(final Integer id) {
    final Optional<Role> rol = this.repository.findById(id);
    return rol.map(value -> RoleUtils.buildGenericResponseSuccess(this.mapper.destinationToSource(value)))
        .orElseGet(RoleUtils::buildGenericResponseError);
  }
}
