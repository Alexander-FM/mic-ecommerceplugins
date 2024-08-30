package com.codecorecix.ecommerce.order.info.mapper;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailFieldsMapper {

  OrderDetail sourceToDestination(final OrderDetailRequestDto source);

  OrderDetailResponseDto destinationToSource(final OrderDetail destination);

  List<OrderDetailResponseDto> toDto(final List<OrderDetail> entityList);
}