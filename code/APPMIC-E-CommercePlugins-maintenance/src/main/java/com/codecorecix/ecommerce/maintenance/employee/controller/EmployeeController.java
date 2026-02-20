package com.codecorecix.ecommerce.maintenance.employee.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.maintenance.employee.service.EmployeeService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("${app.endpoints.employee}")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService service;

  @GetMapping
  public ResponseEntity<GenericResponse<List<EmployeeResponseDto>>> getAllEmployees() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.getAllEmployees());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> saveEmployee(@RequestBody final EmployeeRequestDto employeeRequestDto) {
    if (ObjectUtils.isNotEmpty(employeeRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      MaintenanceUtils.validRequestDto(employeeRequestDto);
      GenericResponse<EmployeeResponseDto> employeeResponseDtoGenericResponse = this.service.save(employeeRequestDto, false);
      if (employeeResponseDtoGenericResponse.getRpta().equals(GenericResponseConstants.RPTA_WARNING)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseDtoGenericResponse);
      }
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(employeeRequestDto, false));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> updateEmployee(@PathVariable(value = "id") final Integer id,
    @RequestBody final EmployeeRequestDto employeeRequestDto) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      employeeRequestDto.setId(id);
      MaintenanceUtils.validRequestDto(employeeRequestDto);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.save(employeeRequestDto, true));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> updateEmployeeStatus(@PathVariable(value = "id") final Integer id,
    @RequestParam(value = "isActive") final Boolean isActive) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.updateEmployeeStatus(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> deleteEmployeeById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteEmployeeById(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
