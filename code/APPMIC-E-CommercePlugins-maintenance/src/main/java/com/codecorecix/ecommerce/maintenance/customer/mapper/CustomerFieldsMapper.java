package com.codecorecix.ecommerce.maintenance.customer.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Customer;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerFieldsMapper {

  Customer sourceToDestination(final CustomerRequestDto source);

  CustomerResponseDto destinationToSource(final Customer destination);

  List<CustomerResponseDto> toDto(final List<Customer> entityList);
}
