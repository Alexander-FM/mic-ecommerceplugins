package com.codecorecix.ecommerce.maintenance.employee.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.entities.Employee;
import com.codecorecix.ecommerce.event.entities.User;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.maintenance.employee.mapper.EmployeeFieldsMapper;
import com.codecorecix.ecommerce.maintenance.employee.repository.EmployeeRepository;
import com.codecorecix.ecommerce.maintenance.employee.utils.EmployeeConstants;
import com.codecorecix.ecommerce.maintenance.user.mapper.UserFieldsMapper;
import com.codecorecix.ecommerce.maintenance.user.repository.UserRepository;
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

  private final UserFieldsMapper userMapper;

  private final UserRepository userRepository;

  @Override
  public GenericResponse<List<EmployeeResponseDto>> getAllEmployees() {
    List<Employee> employees = this.repository.findAll().stream().toList();
    final List<Integer> userIds = employees.stream().map(Employee::getUserId).distinct().toList();
    final Map<Integer, User> userMap =
      this.userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));
    return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.FIND_MESSAGE, employees.stream().map(employee -> {
      final EmployeeResponseDto responseDto = this.mapper.destinationToSource(employee);
      final User user = userMap.get(employee.getUserId());
      if (user != null) {
        responseDto.setUserResponseDto(this.userMapper.destinationToSource(user));
      }
      return responseDto;
    }).toList());
  }

  @Override
  public GenericResponse<EmployeeResponseDto> save(final EmployeeRequestDto employeeRequestDto, final boolean isUpdated) {
    final Optional<User> user = this.userRepository.findById(employeeRequestDto.getUserId());
    if (user.isEmpty()) {
      return GenericUtils.buildGenericResponseError(EmployeeConstants.NOT_EXIST_USER_FOR_EMPLOYEE, null);
    }
    if (this.repository.existByUserIdAndIdNot(employeeRequestDto.getUserId(),
      employeeRequestDto.getId() != null ? employeeRequestDto.getId() : 0)) {
      return GenericUtils.buildGenericResponseError(EmployeeConstants.EMPLOYEE_CONFLICT, null);
    }
    employeeRequestDto.setUserId(user.get().getId());
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
    final EmployeeResponseDto responseDto = this.mapper.destinationToSource(this.repository.save(employeeMapped));
    responseDto.setUserResponseDto(this.userMapper.destinationToSource(user.get()));
    return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.SAVE_MESSAGE, responseDto);
  }

  @Override
  public GenericResponse<EmployeeResponseDto> deleteEmployeeById(final Integer id) {
    final Optional<Employee> employee = this.repository.findById(id);
    return employee.map(value -> {
      this.repository.deleteById(id);
      return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.DELETE_MESSAGE, this.mapper.destinationToSource(value));
    }).orElseGet(() -> GenericUtils.buildGenericResponseError(EmployeeConstants.DELETE_MESSAGE_ERROR, null));
  }

  @Override
  @Transactional
  public GenericResponse<EmployeeResponseDto> updateEmployeeStatus(final Boolean isActive, final Integer id) {
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
    if (employee.isEmpty()) {
      return GenericUtils.buildGenericResponseError(EmployeeConstants.NO_EXIST, null);
    }
    final Optional<User> user = this.userRepository.findById(employee.get().getUserId());
    if (user.isEmpty()) {
      return GenericUtils.buildGenericResponseError(EmployeeConstants.NOT_EXIST_USER_FOR_EMPLOYEE, null);
    }
    final EmployeeResponseDto responseDto = this.mapper.destinationToSource(employee.get());
    responseDto.setUserResponseDto(this.userMapper.destinationToSource(user.get()));
    return GenericUtils.buildGenericResponseSuccess(EmployeeConstants.FIND_MESSAGE, responseDto);
  }
}
