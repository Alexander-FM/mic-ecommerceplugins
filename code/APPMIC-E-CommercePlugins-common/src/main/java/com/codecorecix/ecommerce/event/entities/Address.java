package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {

  private String type;

  @ToString.Exclude
  private String name;

  @ToString.Exclude
  private String surname;

  @ToString.Exclude
  private String name2;

  private String address1;

  private String address2;

  private String address3;
}
