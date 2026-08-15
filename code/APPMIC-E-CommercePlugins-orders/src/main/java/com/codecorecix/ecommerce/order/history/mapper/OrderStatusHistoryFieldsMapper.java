package com.codecorecix.ecommerce.order.history.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderStatusHistory;
import com.codecorecix.ecommerce.order.history.api.dto.request.OrderStatusHistoryRequestDto;
import com.codecorecix.ecommerce.order.history.api.dto.response.OrderStatusHistoryResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderStatusHistoryFieldsMapper {

  @Mapping(target = "order.id", source = "order.id")
  @Mapping(target = "order.orderDate", ignore = true)
  @Mapping(target = "order.orderStatus", source = "orderStatus")
  OrderStatusHistory sourceToDestination(final OrderStatusHistoryRequestDto source);

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "orderStatusId", source = "orderStatus.id")
  @Mapping(target = "orderStatusName", source = "orderStatus.statusName")
  OrderStatusHistoryResponseDto destinationToSource(final OrderStatusHistory destination);

  List<OrderStatusHistoryResponseDto> toDto(final List<OrderStatusHistory> entityList);
}