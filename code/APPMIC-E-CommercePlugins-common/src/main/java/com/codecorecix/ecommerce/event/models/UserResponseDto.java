package com.codecorecix.ecommerce.event.models;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Serializable {

  private Integer id;

  private String username;

  private String password;

  private Boolean isActive;

  private List<RoleResponseDto> roles;
}
