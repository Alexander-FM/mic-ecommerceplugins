package com.codecorecix.ecommerce.event.superclass;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Person {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false, length = 1)
  private String gender;

  @Column
  private LocalDateTime birthdate;
}
