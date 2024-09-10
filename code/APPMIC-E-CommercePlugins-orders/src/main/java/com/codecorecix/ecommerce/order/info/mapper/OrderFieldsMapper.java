package com.codecorecix.ecommerce.order.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderFieldsMapper {

  @Mapping(target = "orderDate", ignore = true)
  Order sourceToDestination(final OrderRequestDto source);

  OrderResponseDto destinationToSource(final Order destination);

  List<OrderResponseDto> toDto(final List<Order> orderList);
}