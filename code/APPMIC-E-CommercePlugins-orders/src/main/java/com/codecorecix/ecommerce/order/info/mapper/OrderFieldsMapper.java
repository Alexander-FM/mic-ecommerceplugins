package com.codecorecix.ecommerce.order.info.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderFieldsMapper {

  @Mapping(target = "orderDate", ignore = true)
  @Mapping(target = "orderStatus", ignore = true)
  Order sourceToDestination(final OrderRequestDto source);

  @Mapping(target = "orderStatusName", ignore = true)
  OrderResponseDto destinationToSource(final Order destination);

  @Mapping(target = "orderStatusName", ignore = true)
  List<OrderResponseDto> toDto(final List<Order> orderList);

  @AfterMapping
  default void setOrderStatusName(final Order order, @MappingTarget final OrderResponseDto orderResponseDto) {
    if (Objects.nonNull(order.getOrderStatus())) {
      orderResponseDto.setOrderStatusName(order.getOrderStatus().getStatusName());
    }
  }

  @AfterMapping
  default void setOrderStatusName(final List<Order> orderList, @MappingTarget final List<OrderResponseDto> orderResponseDtos) {
    if (ObjectUtils.isNotEmpty(orderList)) {
      IntStream.range(0, orderList.size()).forEach(i -> {
        final Order order = orderList.get(i);
        final OrderResponseDto dto = orderResponseDtos.get(i);
        setOrderStatusName(order, dto);
      });
    }
  }
}