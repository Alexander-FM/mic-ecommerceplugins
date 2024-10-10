package com.codecorecix.ecommerce.maintenance.customer.controller;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.exception.GenericUnprocessableEntityException;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.maintenance.customer.service.CustomerService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.validation.Valid;
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
@RequestMapping("api/customer")
public class CustomerController {

  private final CustomerService service;

  public CustomerController(CustomerService service) {
    this.service = service;
  }

  @GetMapping("/listCustomer")
  public ResponseEntity<GenericResponse<List<CustomerResponseDto>>> listCustomer() {
    return ResponseEntity.status(HttpStatus.OK).body(this.service.listCustomer());
  }

  @GetMapping("/getById/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> getCustomerById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (Objects.nonNull(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PostMapping("/saveCustomer")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> saveCustomer(
      @Valid @RequestBody final CustomerRequestDto customerRequestDto) {
    if (ObjectUtils.isNotEmpty(customerRequestDto.getId())) {
      throw new GenericUnprocessableEntityException(GenericResponseConstants.UNPROCESSABLE_ENTITY_EXCEPTION);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.service.saveCustomer(customerRequestDto, false));
    }
  }

  @PutMapping("/updateCustomer/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> updateCustomer(@Valid @PathVariable(value = "id") final Integer id,
      @RequestBody final CustomerRequestDto customerRequestDto) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      customerRequestDto.setId(response.getBody().getId());
      return ResponseEntity.status(HttpStatus.OK).body(this.service.saveCustomer(customerRequestDto, true));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @GetMapping("/disabledOrEnabledCustomer/{id}/{isActive}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> disabledOrEnabledCustomer(@PathVariable(value = "id") final Integer id,
      @PathVariable(value = "isActive") final Boolean isActive) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.disabledOrEnabledCustomer(isActive, id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @DeleteMapping("/deleteCustomer/{id}")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> deleteCustomerById(@PathVariable(value = "id") final Integer id) {
    final GenericResponse<CustomerResponseDto> response = this.service.findById(id);
    if (ObjectUtils.isNotEmpty(response.getBody())) {
      return ResponseEntity.status(HttpStatus.OK).body(this.service.deleteCustomer(id));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
