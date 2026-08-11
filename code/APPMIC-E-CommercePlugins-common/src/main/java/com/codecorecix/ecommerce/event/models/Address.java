package com.codecorecix.ecommerce.event.models;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Address implements Serializable {

  private Integer id;

  private String type;

  private String addressName;

  private String residenceNumber;

  private String department;

  private String province;

  private String district;

  private String placeReference;

  private String postalCode;
}
