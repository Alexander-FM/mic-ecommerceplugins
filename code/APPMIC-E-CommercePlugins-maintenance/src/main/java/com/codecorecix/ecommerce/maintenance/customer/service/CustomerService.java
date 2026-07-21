package com.codecorecix.ecommerce.maintenance.customer.service;

import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import java.util.List;

public interface CustomerService {

  /**
   * Method used to list all customers.
   *
   * @return List of CustomerResponseDto.
   */
  GenericResponse<List<CustomerResponseDto>> listCustomers();

  /**
   * Method used to save the customer.
   *
   * @param customerRequestDto The customer request dto.
   * @param isUpdated Is it updated.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> save(final CustomerRequestDto customerRequestDto, final boolean isUpdated);

  /**
   * Method used to delete the customer.
   *
   * @param id The id of the customer.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> deleteCustomerById(final Integer id);

  /**
   * Method used to update the status of the customer.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> updateCustomerStatus(final Boolean isActive, final Integer id);

  /**
   * Method used to find the customer by id.
   *
   * @param id The id of the customer.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> findById(final Integer id);

  /**
   * Method used to find the customer by username.
   *
   * @param username The username of the customer.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> findByUsername(final String username);
}