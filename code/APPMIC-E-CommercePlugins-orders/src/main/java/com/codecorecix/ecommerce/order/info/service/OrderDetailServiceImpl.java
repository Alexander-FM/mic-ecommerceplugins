package com.codecorecix.ecommerce.order.info.service;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderDetailResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderDetailFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderDetailRepository;
import com.codecorecix.ecommerce.order.info.utils.OrderDetailsConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;

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

  public GenericResponse<List<OrderDetailResponseDto>> getDetailsByOrderId(final Integer orderId) {
    final List<OrderDetail> orderDetail = this.orderDetailRepository.findByOrderId(orderId);
    if (ObjectUtils.isEmpty(orderDetail)) {
      return GenericUtils.buildGenericResponseError(OrderDetailsConstants.NO_EXIST_ORDER_ID_IN_BD, null);
    }
    return GenericUtils.buildGenericResponseSuccess(OrderDetailsConstants.DETAILS_ORDER_FOUND,
        this.orderDetailFieldsMapper.toDto(orderDetail));
  }
}