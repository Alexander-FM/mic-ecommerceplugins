package com.codecorecix.ecommerce.maintenance.customer.service;

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
  public GenericResponse<CustomerResponseDto> saveCustomer(final CustomerRequestDto customerRequestDto) {
    final Customer customer = this.repository.save(this.mapper.sourceToDestination(customerRequestDto));
    return GenericUtils.buildGenericResponseSuccess(CustomerConstants.SAVE_MESSAGE, this.mapper.destinationToSource(customer));
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
  public GenericResponse<CustomerResponseDto> desactivateOrActivateCustomer(final Boolean isActive, final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(value -> {
      this.repository.desactivateOrActivateCustomer(isActive, id);
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
