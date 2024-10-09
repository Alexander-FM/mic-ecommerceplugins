package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "ProductDetails")
public class ProductDetail implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String name;

  @Column
  private String description;

  /**
   * <p>Para la relación @ManyToOne y @OneToOne, el tipo de carga por defecto es EAGER (ansiosa). Esto significa que la entidad relacionada se cargará
   * automáticamente junto con la entidad principal.</p>
   */
  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_ProductsDetails_Products"))
  private Product product;
}
