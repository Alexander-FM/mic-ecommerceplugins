package com.codecorecix.ecommerce.maintenance.employee.api.dto.request;

import java.io.Serializable;
import java.util.Date;

import com.codecorecix.ecommerce.event.entities.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDto implements Serializable {

  private Integer id;

  private String name;

  private String lastName;

  private String gender;

  private Date birthdate;

  private String email;

  private String phoneNumber;

  private Address address;

  private Boolean isActive;
}
