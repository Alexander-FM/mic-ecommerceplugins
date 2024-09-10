package com.codecorecix.ecommerce.event.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders", uniqueConstraints = @UniqueConstraint(columnNames = "orderDate", name = "UK_Orders_orderDate"))
public class Order implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  @CreationTimestamp
  private LocalDateTime orderDate;

  @Column
  private Integer customerId;

  @Column
  private Integer employeeId;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_Orders_OrderStatus"))
  private OrderStatus orderStatus;

  @Column
  private Double totalAmount;

  @Column
  private String orderNotes;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<OrderDetail> orderDetails;
}
