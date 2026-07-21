package com.codecorecix.ecommerce.maintenance.employee.service;

import com.codecorecix.ecommerce.maintenance.employee.api.dto.request.EmployeeRequestDto;
import com.codecorecix.ecommerce.maintenance.employee.api.dto.response.EmployeeResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import java.util.List;

public interface EmployeeService {

  /**
   * Method used to list all employees.
   *
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<List<EmployeeResponseDto>> getAllEmployees();

  /**
   * Method used to save the employee.
   *
   * @param employeeRequestDto The employee request dto.
   * @param isUpdated Is it updated.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> save(final EmployeeRequestDto employeeRequestDto, final boolean isUpdated);

  /**
   * Method used to delete the employee.
   *
   * @param id The id of the employee.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> deleteEmployeeById(final Integer id);

  /**
   * Method used to update the status of the employee.
   *
   * @param isActive True if is active or false en otherwise.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> updateEmployeeStatus(final Boolean isActive, final Integer id);

  /**
   * Method used to find the employee by id.
   *
   * @param id The id of the employee.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> findById(final Integer id);

  /**
   * Method used to find the employee by username.
   *
   * @param username The username of the employee.
   * @return List of EmployeeResponseDto.
   */
  GenericResponse<EmployeeResponseDto> findByUsername(final String username);
}