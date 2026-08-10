package com.codecorecix.ecommerce.event.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

  private Integer id;

  private String username;

  private String password;

  private Boolean isActive;

  private List<RoleRequestDto> roles;
}
