package com.codecorecix.ecommerce.order.info.api.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;

@Data
public class OrderRequestDto implements Serializable {

  private Integer id;

  @CurrentTimestamp
  private LocalDateTime orderDate;

  @NotNull(message = "The customer id is null, please fill.")
  private Integer customerId;

  @NotNull(message = "The employeeId id is null, please fill.")
  private Integer employeeId;

  private OrderStatusRequestDto orderStatus;

  private Double totalAmount;

  private String orderNotes;

  private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();
}
