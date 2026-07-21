package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "Product_Attributes")
public class ProductAttribute implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ProdAttr_Product"))
  @JsonBackReference // Evita que Jackson serialize al padre desde el hijo
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ProdAttr_Attribute"))
  private Attribute attribute;

  @Column(nullable = false)
  private String value; // Ejemplo: "Sí", "No", "v5.3", "Azul"
}
