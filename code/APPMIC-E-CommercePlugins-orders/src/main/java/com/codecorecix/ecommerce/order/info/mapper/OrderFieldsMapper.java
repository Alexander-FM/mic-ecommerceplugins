package com.codecorecix.ecommerce.order.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderFieldsMapper {

  Order sourceToDestination(final OrderRequestDto source);

  OrderResponseDto destinationToSource(final Order destination, final Integer productId);

  List<OrderResponseDto> toDto(final List<Order> orderList);

  @AfterMapping
  default void afterDestinationToSource(@MappingTarget final OrderResponseDto orderResponseDto, final Order order,
      final Integer productId) {
    if (order.getOrderDetails() != null) {
      orderResponseDto.getOrderDetails().forEach(detail -> {
        detail.setProductId(productId);
        detail.setOrderId(order.getId());
      });
    }
  }

  @AfterMapping
  default void afterToDto(@MappingTarget final List<OrderResponseDto> orderResponseDtos, final List<Order> orderList) {
    for (int i = 0; i < orderList.size(); i++) {
      Order order = orderList.get(i);
      OrderResponseDto orderResponseDto = orderResponseDtos.get(i);
      orderResponseDto.getOrderDetails().forEach(detail -> detail.setOrderId(order.getId()));
    }
  }
}