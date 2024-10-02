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
@Table(name = "Customers", uniqueConstraints = {
    @UniqueConstraint(columnNames = "registrationDate", name = "UK_Customers_registrationDate"),
    @UniqueConstraint(columnNames = "email", name = "UK_Customers_email"),
    @UniqueConstraint(columnNames = "phoneNumberOne", name = "UK_Customers_phoneNumberOne"),
    @UniqueConstraint(columnNames = "phoneNumberTwo", name = "UK_Customers_phoneNumberTwo"),
    @UniqueConstraint(columnNames = "phoneNumberThree", name = "UK_Customers_phoneNumberThree")
})
public class Customer extends Person implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phoneNumberOne;

  @Column
  private String phoneNumberTwo;

  @Column
  private String phoneNumberThree;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "address_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_Customers_Address"))
  private Address address;

  @Column
  private Boolean isActive;

  @Column
  @CreationTimestamp
  private LocalDateTime registrationDate;
}
