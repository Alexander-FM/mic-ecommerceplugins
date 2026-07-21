package com.codecorecix.ecommerce.maintenance.attribute.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.request.AttributeRequestDto;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.response.AttributeResponseDto;
import com.codecorecix.ecommerce.maintenance.attribute.service.AttributeService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.endpoints.product-attribute}")
public class AttributeController {

  private final AttributeService service;

  @GetMapping
  public ResponseEntity<GenericResponse<List<AttributeResponseDto>>> getAll() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getAllAttributes());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<AttributeResponseDto>> getById(@PathVariable final Integer id) {
    final GenericResponse<AttributeResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<AttributeResponseDto>> save(@RequestBody final AttributeRequestDto dto) {
    MaintenanceUtils.validRequestDto(dto);
    if (dto.getId() != null) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dto));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<AttributeResponseDto>> update(@PathVariable final Integer id,
      @Valid @RequestBody final AttributeRequestDto dto) {
    final GenericResponse<AttributeResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      MaintenanceUtils.validRequestDto(dto);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.update(id, dto));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<AttributeResponseDto>> delete(@PathVariable final Integer id) {
    final GenericResponse<AttributeResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteById(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
