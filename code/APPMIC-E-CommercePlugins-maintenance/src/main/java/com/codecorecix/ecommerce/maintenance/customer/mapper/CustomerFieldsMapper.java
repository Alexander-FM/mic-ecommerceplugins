package com.codecorecix.ecommerce.maintenance.customer.mapper;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.event.entities.Address;
import com.codecorecix.ecommerce.event.entities.Customer;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.request.CustomerRequestDto;
import com.codecorecix.ecommerce.maintenance.customer.api.dto.response.CustomerResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CustomerFieldsMapper {

  @Mapping(target = "userRegistration", ignore = true)
  @Mapping(target = "registrationDate", ignore = true)
  @Mapping(target = "userModification", ignore = true)
  @Mapping(target = "modificationDate", ignore = true)
  Customer sourceToDestination(final CustomerRequestDto source);

  @Mapping(target = "addressName", source = "address", qualifiedByName = "concatenateAddress")
  CustomerResponseDto destinationToSource(final Customer destination);

  @Mapping(target = "addressName", source = "address", qualifiedByName = "concatenateAddress")
  List<CustomerResponseDto> toDto(final List<Customer> entityList);

  @Named("concatenateAddress")
  default String concatenateAddress(final Address address) {
    if (Objects.isNull(address)) {
      return "Has no address";
    }
    return StringUtils.joinWith(GenericResponseConstants.COMMA, StringUtils.joinWith(GenericResponseConstants.SPACE,
        address.getAddressName(), address.getResidenceNumber()), StringUtils.upperCase(
        StringUtils.joinWith(GenericResponseConstants.COMMA, address.getProvince(), address.getDistrict(), address.getPostalCode(),
            address.getDepartment())
    ));
  }
}
