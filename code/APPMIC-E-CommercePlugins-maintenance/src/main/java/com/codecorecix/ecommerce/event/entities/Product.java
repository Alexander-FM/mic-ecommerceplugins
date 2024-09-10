package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "Products")
public class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String barCode;

  @Column
  private String name;

  @Column(length = 1500)
  private String description;

  @Column
  private Double price;

  @Column
  private Integer stock;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_Products_Categories"))
  private Category category;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_Products_Brands"))
  private Brand brand;

  @Column
  private Boolean isActive;

  @Column
  private Boolean isRecommended;

  /**
   * <p>En JPA (Java Persistence API), el tipo de carga por defecto para las relaciones @OneToMany y @ManyToMany es LAZY (perezosa).</p>
   * <p>Recuerda, no es obligatorio especificar un tipo de carga, pero es buena práctica ser explícito para que el código sea más legible y
   * fácil de mantener.</p>
   */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductImage> images;

  /**
   * <p>En JPA (Java Persistence API), el tipo de carga por defecto para las relaciones @OneToMany y @ManyToMany es LAZY (perezosa).</p>
   * <p>Recuerda, no es obligatorio especificar un tipo de carga, pero es buena práctica ser explícito para que el código sea más legible y
   * fácil de mantener.</p>
   */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductDetail> details;
}
