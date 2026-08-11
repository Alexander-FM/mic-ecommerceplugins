package com.codecorecix.ecommerce.api.dto.request;

import com.codecorecix.ecommerce.event.models.CustomerRequestDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterRequestDto {
  private String username;
  private String password;
  private CustomerRequestDto customer;
}
