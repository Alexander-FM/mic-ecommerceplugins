package com.codecorecix.ecommerce.order.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailFieldsMapper {

  @Mapping(target = "order", ignore = true)
  OrderDetail sourceToDestination(final OrderDetailRequestDto source);

  @Mapping(target = "orderId", source = "order.id")
  OrderDetailResponseDto destinationToSource(final OrderDetail destination);

  @Mapping(target = "orderId", ignore = true)
  List<OrderDetailResponseDto> toDto(final List<OrderDetail> entityList);

  @AfterMapping
  default void afterToDto(@MappingTarget final List<OrderDetailResponseDto> orderDetailResponseDto, final List<OrderDetail> orderDetails) {
    for (OrderDetail orderDetail : orderDetails) {
      orderDetailResponseDto.forEach(orderDetailResponseDto1 -> orderDetailResponseDto1.setOrderId(orderDetail.getOrder().getId()));
    }
  }
}