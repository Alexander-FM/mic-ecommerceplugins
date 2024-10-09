package com.codecorecix.ecommerce.maintenance.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Customer;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.maintenance.customer.mapper.CustomerFieldsMapper;
import com.codecorecix.ecommerce.maintenance.customer.repository.CustomerRepository;
import com.codecorecix.ecommerce.maintenance.customer.utils.CustomerConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  private final CustomerFieldsMapper mapper;

  @Override
  public GenericResponse<List<CustomerResponseDto>> listCustomer() {
    return GenericUtils.buildGenericResponseSuccess(null, this.mapper.toDto(this.repository.findAll()));
  }

  @Override
  public GenericResponse<CustomerResponseDto> saveCustomer(final CustomerRequestDto customerRequestDto, final boolean isUpdated) {
    final Customer customerMapped = this.mapper.sourceToDestination(customerRequestDto);
    if (isUpdated) {
      final Customer customerBD = this.repository.findById(customerRequestDto.getId()).orElseThrow();
      customerMapped.setUserModification("UserModification");
      customerMapped.setModificationDate(LocalDateTime.now());
      customerMapped.setUserRegistration(customerBD.getUserRegistration());
      customerMapped.setRegistrationDate(customerBD.getRegistrationDate());
      customerMapped.getAddress().setId(customerBD.getAddress().getId());
    }
    customerMapped.setUserRegistration("UserRegistration");
    return GenericUtils.buildGenericResponseSuccess(CustomerConstants.SAVE_MESSAGE,
        this.mapper.destinationToSource(this.repository.save(customerMapped)));
  }

  @Override
  public GenericResponse<CustomerResponseDto> deleteCustomer(final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(value -> {
      this.repository.deleteById(id);
      return GenericUtils.buildGenericResponseSuccess(CustomerConstants.DELETE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(CustomerConstants.DELETE_MESSAGE_ERROR, null));
  }

  @Override
  @Transactional
  public GenericResponse<CustomerResponseDto> disabledOrEnabledCustomer(final Boolean isActive, final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(value -> {
      this.repository.disabledOrEnabledCustomer(isActive, id);
      return GenericUtils.buildGenericResponseSuccess(CustomerConstants.UPDATE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(CustomerConstants.UPDATE_MESSAGE_ERROR, null));
  }

  @Override
  public GenericResponse<CustomerResponseDto> findById(final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(
            value -> GenericUtils.buildGenericResponseSuccess(CustomerConstants.FIND_MESSAGE, this.mapper.destinationToSource(value)))
        .orElseGet(() -> GenericUtils.buildGenericResponseError(CustomerConstants.FIND_MESSAGE_ERROR, null));
  }
}
