package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Address")
public class Address implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String addressName;

  @Column(nullable = false)
  private String residenceNumber;

  @Column(nullable = false)
  private String department;

  @Column(nullable = false)
  private String province;

  @Column(nullable = false)
  private String district;

  @Column(nullable = false)
  private String placeReference;

  @Column(nullable = false)
  private String postalCode;
}
