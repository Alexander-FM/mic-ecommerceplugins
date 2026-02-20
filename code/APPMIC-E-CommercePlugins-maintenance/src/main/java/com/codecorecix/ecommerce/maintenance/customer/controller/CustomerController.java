package com.codecorecix.ecommerce.maintenance.customer.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.maintenance.customer.service.CustomerService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.MaintenanceUtils;

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
@RequestMapping("${app.endpoints.customer}")
public class CustomerController {

  private final CustomerService service;

  public CustomerController(CustomerService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<CustomerResponseDto>>> listCustomers() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listCustomers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> getCustomerById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping
  public ResponseEntity<GenericResponse<CustomerResponseDto>> saveCustomer(@RequestBody final CustomerRequestDto customerRequestDto) {
    if (ObjectUtils.isNotEmpty(customerRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      MaintenanceUtils.validRequestDto(customerRequestDto);
      GenericResponse<CustomerResponseDto> customerResponseDtoGenericResponse = this.service.save(customerRequestDto, false);
      if (customerResponseDtoGenericResponse.getRpta().equals(GenericResponseConstants.RPTA_WARNING)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customerResponseDtoGenericResponse);
      }
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(customerRequestDto, false));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> updateCustomer(@PathVariable(value = "id") final Integer id,
    @RequestBody final CustomerRequestDto customerRequestDto) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      customerRequestDto.setId(response.getBody().getId());
      MaintenanceUtils.validRequestDto(customerRequestDto);
      return ResponseEntity.status(HttpStatus.OK).body(this.service.save(customerRequestDto, true));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> updateCustomerStatus(@PathVariable(value = "id") final Integer id,
    @RequestParam(value = "isActive") final Boolean isActive) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.updateCustomerStatus(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> deleteCustomerById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteCustomerById(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
