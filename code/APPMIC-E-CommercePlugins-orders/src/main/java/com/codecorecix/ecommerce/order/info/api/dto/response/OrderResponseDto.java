package com.codecorecix.ecommerce.order.info.api.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderResponseDto implements Serializable {

  private Integer id;

  private LocalDateTime orderDate;

  private Integer customerId;

  private String deliveryAddressName;

  private String receivedBy;

  private Integer employeeId;

  private String orderStatusName;

  private Double totalAmount;

  private String orderNotes;
}
