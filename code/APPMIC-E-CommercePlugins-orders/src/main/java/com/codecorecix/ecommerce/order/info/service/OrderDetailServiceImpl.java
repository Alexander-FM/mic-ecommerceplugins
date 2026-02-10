package com.codecorecix.ecommerce.order.info.service;

import java.util.ArrayList;
import java.util.List;

import com.codecorecix.ecommerce.event.clients.MaintenanceClientRest;
import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.exceptions.OrderException;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderDetailFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderDetailRepository;
import com.codecorecix.ecommerce.order.info.utils.OrderDetailsConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;
import com.codecorecix.ecommerce.utils.OrderErrorMessage;
import com.codecorecix.ecommerce.utils.OrdersUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

  private final OrderDetailRepository orderDetailRepository;

  private final OrderDetailFieldsMapper orderDetailFieldsMapper;

  private final MaintenanceClientRest maintenanceClientRest;

  @Override
  public void saveOrderDetails(final List<OrderDetailRequestDto> orderDetailRequestDto, final Integer orderId, final String token) {
    try {
      OrdersUtils.validRequestDto(orderDetailRequestDto);
      GenericResponse<List<ProductInfo>> response = this.maintenanceClientRest.checkProducts(
          orderDetailRequestDto.stream().map(OrderDetailRequestDto::getProductId).toList(), token);
      if (response.getBody().isEmpty()) {
        throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_NOT_AVAILABLE);
      }
      final List<OrderDetail> orderDetails = new ArrayList<>();
      for (final OrderDetailRequestDto orderDetail : orderDetailRequestDto) {
        final OrderDetail orderDetailEntity = this.orderDetailFieldsMapper.toEntity(orderDetail, orderId);
        orderDetails.add(orderDetailEntity);
      }
      this.orderDetailRepository.saveAll(orderDetails);
    } catch (final OrderException e) {
      throw new OrderException(e.getErrorMessage());
    }
  }

  public GenericResponse<List<OrderDetailResponseDto>> getDetailsByOrderId(final Integer orderId) {
    final List<OrderDetail> orderDetail = this.orderDetailRepository.findByOrderId(orderId);
    if (ObjectUtils.isEmpty(orderDetail)) {
      return GenericUtils.buildGenericResponseError(OrderDetailsConstants.NO_EXIST_ORDER_ID_IN_BD, null);
    }
    return GenericUtils.buildGenericResponseSuccess(OrderDetailsConstants.DETAILS_ORDER_FOUND,
        this.orderDetailFieldsMapper.toDto(orderDetail));
  }
}
