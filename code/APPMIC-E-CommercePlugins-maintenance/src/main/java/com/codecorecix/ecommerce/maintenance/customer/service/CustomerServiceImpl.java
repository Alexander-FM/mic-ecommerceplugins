package com.codecorecix.ecommerce.maintenance.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.entities.Customer;
import com.codecorecix.ecommerce.event.entities.User;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.maintenance.customer.mapper.CustomerFieldsMapper;
import com.codecorecix.ecommerce.maintenance.customer.repository.CustomerRepository;
import com.codecorecix.ecommerce.maintenance.customer.utils.CustomerConstants;
import com.codecorecix.ecommerce.maintenance.user.mapper.UserFieldsMapper;
import com.codecorecix.ecommerce.maintenance.user.repository.UserRepository;
import com.codecorecix.ecommerce.maintenance.user.utils.UserConstants;
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

  private final UserRepository userRepository;

  private final UserFieldsMapper userMapper;

  @Override
  public GenericResponse<List<CustomerResponseDto>> listCustomers() {
    List<Customer> customers = this.repository.findAll().stream().toList();
    final List<Integer> userIds = customers.stream().map(Customer::getUserId).distinct().toList();
    final Map<Integer, User> userMap =
      this.userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));
    return GenericUtils.buildGenericResponseSuccess(null, customers.stream().map(customer -> {
      final CustomerResponseDto responseDto = this.mapper.destinationToSource(customer);
      final User user = userMap.get(customer.getUserId());
      if (user != null) {
        responseDto.setUserResponseDto(this.userMapper.destinationToSource(user));
      }
      return responseDto;
    }).toList());
  }

  @Override
  public GenericResponse<CustomerResponseDto> save(final CustomerRequestDto customerRequestDto, final boolean isUpdated) {
    final Optional<User> user = this.userRepository.findById(customerRequestDto.getUserId());
    if (user.isEmpty()) {
      return GenericUtils.buildGenericResponseError(CustomerConstants.NOT_EXIST_USER_FOR_CUSTOMER, null);
    }
    if (this.repository.existByUserIdAndIdNot(customerRequestDto.getUserId(),
      customerRequestDto.getId() != null ? customerRequestDto.getId() : 0)) {
      return GenericUtils.buildGenericResponseError(CustomerConstants.EMPLOYEE_CONFLICT, null);
    }
    customerRequestDto.setUserId(user.get().getId());
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
    final CustomerResponseDto customerResponseDto = this.mapper.destinationToSource(this.repository.save(customerMapped));
    customerResponseDto.setUserResponseDto(this.userMapper.destinationToSource(user.get()));
    return GenericUtils.buildGenericResponseSuccess(CustomerConstants.SAVE_MESSAGE, customerResponseDto);
  }

  @Override
  public GenericResponse<CustomerResponseDto> deleteCustomerById(final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(value -> {
      this.repository.deleteById(id);
      return GenericUtils.buildGenericResponseSuccess(CustomerConstants.DELETE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(CustomerConstants.DELETE_MESSAGE_ERROR, null));
  }

  @Override
  @Transactional
  public GenericResponse<CustomerResponseDto> updateCustomerStatus(final Boolean isActive, final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    return customer.map(value -> {
      this.repository.disabledOrEnabledCustomer(isActive, id);
      return GenericUtils.buildGenericResponseSuccess(CustomerConstants.UPDATE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(CustomerConstants.UPDATE_MESSAGE_ERROR, null));
  }

  @Override
  public GenericResponse<CustomerResponseDto> findById(final Integer id) {
    final Optional<Customer> customer = this.repository.findById(id);
    if(customer.isEmpty()) {
      return GenericUtils.buildGenericResponseError(CustomerConstants.FIND_MESSAGE_ERROR, null);
    }
    Optional<User> user = this.userRepository.findById(customer.get().getUserId());
    if(user.isEmpty()) {
      return GenericUtils.buildGenericResponseError(CustomerConstants.NOT_EXIST_USER_FOR_CUSTOMER, null);
    }
    final CustomerResponseDto responseDto = this.mapper.destinationToSource(customer.get());
    responseDto.setUserResponseDto(this.userMapper.destinationToSource(user.get()));
    return GenericUtils.buildGenericResponseSuccess(CustomerConstants.FIND_MESSAGE, responseDto);
  }
}
