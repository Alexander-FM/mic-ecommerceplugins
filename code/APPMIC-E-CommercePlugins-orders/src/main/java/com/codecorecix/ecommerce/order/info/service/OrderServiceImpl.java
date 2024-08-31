package com.codecorecix.ecommerce.order.info.service;

import java.time.LocalDateTime;

import com.codecorecix.ecommerce.event.clients.MaintenanceClientRest;
import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.event.models.ProductResponseDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderRepository;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final OrderFieldsMapper orderFieldsMapper;

  private final MaintenanceClientRest maintenanceClientRest;

  @Override
  @Transactional
  public GenericResponse<OrderResponseDto> saveOrder(final OrderRequestDto orderRequestDto) {
    try {
      final Order orderInfo = this.orderFieldsMapper.sourceToDestination(orderRequestDto);
      orderInfo.getOrderDetails().forEach(detail -> detail.setOrder(orderInfo));
      orderInfo.setOrderDate(LocalDateTime.now());
      GenericResponse<ProductResponseDto> response =
          this.maintenanceClientRest.getProductById(orderRequestDto.getOrderDetails().getFirst().getProductId());
      final Order orderBD = this.orderRepository.save(orderInfo);
      final OrderResponseDto orderResponseDto = this.orderFieldsMapper.destinationToSource(orderBD, response.getBody().getId());
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, orderResponseDto);
    } catch (Exception e) {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, GenericResponseConstants.WRONG_OPERATION, null);
    }
  }
}
