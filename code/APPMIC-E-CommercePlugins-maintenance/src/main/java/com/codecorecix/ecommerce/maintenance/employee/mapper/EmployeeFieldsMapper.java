package com.codecorecix.ecommerce.maintenance.employee.mapper;

import java.util.List;
import java.util.Objects;

import com.codecorecix.ecommerce.event.entities.Address;
import com.codecorecix.ecommerce.event.entities.Employee;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeeFieldsMapper {

  @Mapping(target = "userRegistration", ignore = true)
  @Mapping(target = "registrationDate", ignore = true)
  @Mapping(target = "userModification", ignore = true)
  @Mapping(target = "modificationDate", ignore = true)
  Employee sourceToDestination(final EmployeeRequestDto source);

  @Mapping(target = "addressName", source = "address", qualifiedByName = "concatenateAddress")
  EmployeeResponseDto destinationToSource(final Employee destination);

  @Mapping(target = "addressName", source = "address", qualifiedByName = "concatenateAddress")
  List<EmployeeResponseDto> toDto(final List<Employee> entityList);

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
