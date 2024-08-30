package com.codecorecix.ecommerce.order.status.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderStatus;
import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;
import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderStatusFieldsMapper {

  OrderStatus sourceToDestination(final OrderStatusRequestDto source);

  OrderStatusResponseDto destinationToSource(final OrderStatus destination);

  List<OrderStatusResponseDto> toDto(final List<OrderStatus> entityList);
}