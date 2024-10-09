package com.codecorecix.ecommerce.maintenance.customer.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface CustomerService {

  /**
   * Method used to list all customers.
   *
   * @return List of CustomerResponseDto.
   */
  GenericResponse<List<CustomerResponseDto>> listCustomer();

  /**
   * Method used to save the customer.
   *
   * @param customerRequestDto The customer request dto.
   * @param isUpdated Is it updated.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> saveCustomer(final CustomerRequestDto customerRequestDto, final boolean isUpdated);

  /**
   * Method used to delete the customer.
   *
   * @param id The id of the customer.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> deleteCustomer(final Integer id);

  /**
   * Method used to desactive or activate the customer.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> disabledOrEnabledCustomer(final Boolean isActive, final Integer id);

  /**
   * Method used to find the customer by id.
   *
   * @param id The id of the customer.
   * @return List of CustomerResponseDto.
   */
  GenericResponse<CustomerResponseDto> findById(final Integer id);
}
