package com.codecorecix.ecommerce.order.info.mapper;

import java.util.Collections;
import java.util.List;

import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderFieldsMapper {

  @Mapping(target = "orderDate", ignore = true)
  @Mapping(target = "orderDetails", source = "orderDetails", qualifiedByName = "mapOrderDetails")
  Order sourceToDestination(final OrderRequestDto source);

  OrderResponseDto destinationToSource(final Order destination);

  List<OrderResponseDto> toDto(final List<Order> orderList);

  @Named("mapOrderDetails")
  default List<OrderDetail> mapOrderDetails(final List<OrderDetailRequestDto> orderDetailDtos) {
    if (orderDetailDtos == null) {
      return Collections.emptyList();
    }
    return orderDetailDtos.stream()
        .map(orderDetailDto -> {
          OrderDetail orderDetail = new OrderDetail();
          orderDetail.setId(orderDetailDto.getId());
          orderDetail.setProductId(orderDetailDto.getProductId());
          orderDetail.setQuantity(orderDetailDto.getQuantity());
          orderDetail.setUnitPrice(orderDetailDto.getUnitPrice());
          orderDetail.setTotalPrice(orderDetailDto.getTotalPrice());
          Order mappedOrder = new Order();
          mappedOrder.setId(orderDetailDto.getOrderId());
          orderDetail.setOrder(mappedOrder);
          return orderDetail;
        }).toList();
  }
}