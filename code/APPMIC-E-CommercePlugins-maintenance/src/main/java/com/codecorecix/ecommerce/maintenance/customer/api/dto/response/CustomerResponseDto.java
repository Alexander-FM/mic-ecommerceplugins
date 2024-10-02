package com.codecorecix.ecommerce.maintenance.customer.api.dto.response;

import java.time.LocalDateTime;
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
public class CustomerResponseDto {

  private String name;

  private String lastName;

  private String gender;

  private Date birthdate;

  private String email;

  private String phoneNumberOne;

  private String phoneNumberTwo;

  private String phoneNumberThree;

  private Address address;

  private Boolean isActive;

  private LocalDateTime registrationDate;
}
