package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "Products", schema = "maintenance_service")
public class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String barCode;

  @Column
  private String name;

  @Column(length = 500)
  private String description;

  @Column
  private Double price;

  @Column
  private Integer stock;

  @ManyToOne
  private Category category;

  @ManyToOne
  private Brand brand;

  @Column
  private Boolean isActive;

  @Column
  private Boolean isRecommended;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<ProductImage> images;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<ProductDetail> details;
}
