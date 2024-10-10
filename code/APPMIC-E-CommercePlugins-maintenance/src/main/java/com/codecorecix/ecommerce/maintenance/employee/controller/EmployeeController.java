package com.codecorecix.ecommerce.maintenance.employee.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.maintenance.employee.service.EmployeeService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
@RequestMapping("api/employee")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService service;

  @GetMapping("/listEmployee")
  public ResponseEntity<GenericResponse<List<EmployeeResponseDto>>> listEmployee() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listEmployee());
  }

  @GetMapping("/getById/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping("/saveEmployee")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> saveEmployee(
      @Valid @RequestBody final EmployeeRequestDto employeeRequestDto) {
    if (ObjectUtils.isNotEmpty(employeeRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveEmployee(employeeRequestDto, false));
    }
  }

  @PutMapping("/updateEmployee/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> updateEmployee(@Valid @PathVariable(value = "id") final Integer id,
      @RequestBody final EmployeeRequestDto employeeRequestDto) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      employeeRequestDto.setId(response.getBody().getId());
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveEmployee(employeeRequestDto, true));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/disabledOrEnabledEmployee/{id}/{isActive}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> disabledOrEnabledEmployee(@PathVariable(value = "id") final Integer id,
      @PathVariable(value = "isActive") final Boolean isActive) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.disabledOrEnabledEmployee(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/deleteEmployee/{id}")
  public ResponseEntity<GenericResponse<EmployeeResponseDto>> deleteEmployeeById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<EmployeeResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteEmployee(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
