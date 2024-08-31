package com.codecorecix.ecommerce.order.info.api.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;

import lombok.Data;

@Data
public class OrderResponseDto implements Serializable {

  private Integer id;

  private LocalDateTime orderDate;

  private Integer customerId;

  private Integer employeeId;

  private OrderStatusResponseDto orderStatus;

  private Double totalAmount;

  private String orderNotes;

  private List<OrderDetailResponseDto> orderDetails = new ArrayList<>();
}
