package com.codecorecix.ecommerce.maintenance.role.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.role.dto.request.RoleRequestDto;
import com.codecorecix.ecommerce.maintenance.role.dto.response.RoleResponseDto;
import com.codecorecix.ecommerce.maintenance.role.service.RoleService;
import com.codecorecix.ecommerce.maintenance.role.utils.RoleConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.role}")
public class RoleController {

  private final RoleService service;

  public RoleController(final RoleService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<RoleResponseDto>>> getAllRoles() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(this.service.getAllRoles());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<RoleResponseDto>> getRoleById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<RoleResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(response);
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(response);
    }
  }

  @GetMapping("/internal/roleName/{roleName}")
  public ResponseEntity<GenericResponse<RoleResponseDto>> getRoleByName(@PathVariable(value = "roleName") final String roleName) {
    final GenericResponse<RoleResponseDto> response = this.service.findByName(roleName);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(response);
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<RoleResponseDto>> saveRole(@RequestBody final RoleRequestDto roleRequestDto) {
    if (ObjectUtils.isNotEmpty(roleRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(RoleConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      MaintenanceUtils.validRequestDto(roleRequestDto);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(this.service.save(roleRequestDto));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<RoleResponseDto>> updateBrand(@Valid @PathVariable(value = "id") final Integer id,
                                                                      @RequestBody final RoleRequestDto roleRequestDto) {
    final GenericResponse<RoleResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      roleRequestDto.setId(id);
      MaintenanceUtils.validRequestDto(roleRequestDto);
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(this.service.save(roleRequestDto));
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(response);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<GenericResponse<RoleResponseDto>> updateRoleStatus(@PathVariable(value = "id") final Integer id,
                                                                           @RequestParam(value = "isActive") final Boolean isActive) {
    final GenericResponse<RoleResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(this.service.updateBrandStatus(isActive, id));
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<RoleResponseDto>> deleteRole(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<RoleResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(this.service.deleteById(id));
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(response);
    }
  }
}
