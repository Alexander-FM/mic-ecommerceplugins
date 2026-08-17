package com.codecorecix.ecommerce.order.info.api.dto.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto implements Serializable {

  private Integer id;

  @NotNull(message = "The customerId is null, please fill")
  private Integer customerId;

  private String deliveryAddressName;

  private Integer employeeId;

  private Double totalAmount;

  private String orderNotes;

  @NotEmpty(message = "The orderDetails is null, please fill")
  private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();

  public OrderRequestDto(Integer id) {
    this.id = id;
  }
}
