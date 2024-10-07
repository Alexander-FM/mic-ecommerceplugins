package com.codecorecix.ecommerce.maintenance.employee.api.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto implements Serializable {

  private Integer id;

  private String name;

  private String lastName;

  private String gender;

  private Date birthdate;

  private String email;

  private String phoneNumber;

  private String addressName;

  private Boolean isActive;

  private String userRegistration;

  private LocalDateTime registrationDate;

  private String userModification;

  private LocalDateTime modificationDate;
}
