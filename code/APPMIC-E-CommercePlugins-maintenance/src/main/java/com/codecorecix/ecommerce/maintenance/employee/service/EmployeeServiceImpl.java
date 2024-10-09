package com.codecorecix.ecommerce.maintenance.employee.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Employee;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.maintenance.employee.mapper.EmployeeFieldsMapper;
import com.codecorecix.ecommerce.maintenance.employee.repository.EmployeeRepository;
import com.codecorecix.ecommerce.maintenance.employee.utils.EmployeeConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository repository;

  private final EmployeeFieldsMapper mapper;

  @Override
  public GenericResponse<List<EmployeeResponseDto>> listEmployee() {
    return GenericUtils.buildGenericResponseSuccess(null, this.mapper.toDto(this.repository.findAll()));
  }

  @Override
  public GenericResponse<EmployeeResponseDto> saveEmployee(final EmployeeRequestDto employeeRequestDto, final boolean isUpdated) {
    final Employee employeeMapped = this.mapper.sourceToDestination(employeeRequestDto);
    if (isUpdated) {
      final Employee employeeBD = this.repository.findById(employeeRequestDto.getId()).orElseThrow();
      employeeMapped.setUserModification("UserModification");
      employeeMapped.setModificationDate(LocalDateTime.now());
      employeeMapped.setUserRegistration(employeeBD.getUserRegistration());
      employeeMapped.setRegistrationDate(employeeBD.getRegistrationDate());
      employeeMapped.getAddress().setId(employeeBD.getAddress().getId());
    }
    employeeMapped.setUserRegistration("UserRegistration");
    return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.SAVE_MESSAGE,
        this.mapper.destinationToSource(this.repository.save(employeeMapped)));
  }

  @Override
  public GenericResponse<EmployeeResponseDto> deleteEmployee(final Integer id) {
    final Optional<Employee> employee = this.repository.findById(id);
    return employee.map(value -> {
      this.repository.deleteById(id);
      return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.DELETE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(EmployeeConstants.DELETE_MESSAGE_ERROR, null));
  }

  @Override
  @Transactional
  public GenericResponse<EmployeeResponseDto> disabledOrEnabledEmployee(final Boolean isActive, final Integer id) {
    final Optional<Employee> employee = this.repository.findById(id);
    return employee.map(value -> {
      this.repository.disabledOrEnabledEmployee(isActive, id);
      value.setIsActive(isActive);
      return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.UPDATE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(EmployeeConstants.UPDATE_MESSAGE_ERROR, null));
  }

  @Override
  public GenericResponse<EmployeeResponseDto> findById(final Integer id) {
    final Optional<Employee> employee = this.repository.findById(id);
    return employee.map(
            value -> GenericUtils.buildGenericResponseSuccess(EmployeeConstants.FIND_MESSAGE, this.mapper.destinationToSource(value)))
        .orElseGet(() -> GenericUtils.buildGenericResponseError(EmployeeConstants.FIND_MESSAGE_ERROR, null));
  }
}
