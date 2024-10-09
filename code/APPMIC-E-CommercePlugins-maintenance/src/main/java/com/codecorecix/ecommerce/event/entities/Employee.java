package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.codecorecix.ecommerce.event.superclass.Person;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Employees", uniqueConstraints = {
    @UniqueConstraint(columnNames = "registrationDate", name = "UK_Customers_registrationDate"),
    @UniqueConstraint(columnNames = "email", name = "UK_Customers_email"),
    @UniqueConstraint(columnNames = "phoneNumber", name = "UK_Customers_phoneNumber")
})
public class Employee extends Person implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phoneNumber;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "address_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_Employee_Address"))
  private Address address;

  @Column
  private Boolean isActive;

  @Column
  private String userRegistration;

  @Column
  @CreationTimestamp
  private LocalDateTime registrationDate;

  @Column
  private String userModification;

  @Column
  private LocalDateTime modificationDate;
}
