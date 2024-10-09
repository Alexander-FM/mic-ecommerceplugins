package com.codecorecix.ecommerce.maintenance.employee.service;

import java.util.List;

import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

public interface EmployeeService {

  /**
   * Method used to list all employees.
   *
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<List<EmployeeResponseDto>> listEmployee();

  /**
   * Method used to save the employee.
   *
   * @param employeeRequestDto The employee request dto.
   * @param isUpdated Is it updated.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> saveEmployee(final EmployeeRequestDto employeeRequestDto, final boolean isUpdated);

  /**
   * Method used to delete the employee.
   *
   * @param id The id of the employee.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> deleteEmployee(final Integer id);

  /**
   * Method used to desactive or activate the employee.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> disabledOrEnabledEmployee(final Boolean isActive, final Integer id);

  /**
   * Method used to find the employee by id.
   *
   * @param id The id of the employee.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> findById(final Integer id);
}
