package com.codecorecix.ecommerce.event.models;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDto implements Serializable {

  private Integer id;

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

  private Integer userId;

}
