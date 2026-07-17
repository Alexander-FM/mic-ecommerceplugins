package com.codecorecix.ecommerce.order.info.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.clients.MaintenanceClientRest;
import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.exceptions.OrderException;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderDetailFieldsMapper;
import com.codecorecix.ecommerce.order.info.mapper.OrderFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderDetailRepository;
import com.codecorecix.ecommerce.order.info.repository.OrderRepository;
import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;
import com.codecorecix.ecommerce.order.status.service.OrderStatusService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;
import com.codecorecix.ecommerce.utils.OrderErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final OrderFieldsMapper orderFieldsMapper;

  private final OrderStatusService orderStatusService;

  private final MaintenanceClientRest maintenanceClientRest;

  private final OrderDetailRepository orderDetailRepository;

  private final OrderDetailFieldsMapper orderDetailFieldsMapper;

  @Override
  @Transactional
  public GenericResponse<OrderResponseDto> saveOrder(final OrderRequestDto orderRequestDto) {
    List<Integer> productIds = orderRequestDto
        .getOrderDetails()
        .stream()
        .map(OrderDetailRequestDto::getProductId)
        .toList();

    GenericResponse<List<ProductInfo>> response = this.maintenanceClientRest.checkProducts(productIds);

    if (response.getBody() == null || response
        .getBody()
        .isEmpty()) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_NOT_AVAILABLE);
    }

    if (response
        .getBody()
        .size() != productIds.size()) {
      throw new OrderException(OrderErrorMessage.INCONSISTENT_PRODUCT_DATA);
    }

    String productsOutOfStock = response
        .getBody()
        .stream()
        .filter(p -> p.getStock() <= 0)
        .map(ProductInfo::getName)
        .collect(Collectors.joining(", "));

    if (!productsOutOfStock.isEmpty()) {
      throw new OrderException(OrderErrorMessage.PRODUCTS_OUT_OF_STOCK, productsOutOfStock);
    }

    final Map<Integer, ProductInfo> productInfoMap = response
        .getBody()
        .stream()
        .collect(Collectors.toMap(ProductInfo::getId, Function.identity()));

    orderRequestDto
        .getOrderDetails()
        .forEach(detail -> {
          ProductInfo productInfo = productInfoMap.get(detail.getProductId());
          if (productInfo != null) {
            detail.setProductName(productInfo.getName());
            detail.setProductImageUrl(productInfo.getMainImageUrl());
          }
        });

    final Order orderInfo = this.orderFieldsMapper.sourceToDestination(orderRequestDto);
    orderInfo.setOrderDate(LocalDateTime.now(ZoneId.systemDefault()));

    final GenericResponse<OrderStatusResponseDto> findStatusById =
        this.orderStatusService.findById(orderRequestDto
            .getOrderStatus()
            .getId());

    if (findStatusById
        .getRpta()
        .equals(-1)) {
      throw new OrderException(OrderErrorMessage.ERROR_RESOURCE_STATUS_NOT_AVAILABLE);
    }

    final Order orderBD = this.orderRepository.save(orderInfo);

    final List<OrderDetail> orderDetails = new ArrayList<>();
    for (final OrderDetailRequestDto orderDetail : orderRequestDto.getOrderDetails()) {
      final OrderDetail orderDetailEntity = this.orderDetailFieldsMapper.toEntity(orderDetail, orderBD.getId());
      orderDetails.add(orderDetailEntity);
    }
    this.orderDetailRepository.saveAll(orderDetails);

    orderBD
        .getOrderStatus()
        .setStatusName(findStatusById
            .getBody()
            .getStatusName());
    orderBD
        .getOrderStatus()
        .setIsActive(findStatusById
            .getBody()
            .getIsActive());

    final OrderResponseDto orderResponseDto = this.orderFieldsMapper.destinationToSource(orderBD);
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, orderResponseDto);
  }

  public GenericResponse<List<OrderResponseDto>> getAllOrders() {
    final List<Order> orders = this.orderRepository.findAll();
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.orderFieldsMapper.toDto(orders));
  }

  public GenericResponse<OrderResponseDto> getOrderById(final Long orderId) {
    final Optional<Order> order = this.orderRepository.findById(Math.toIntExact(orderId));
    return order
        .map(
            value -> GenericUtils.buildGenericResponseSuccess(StringUtils.EMPTY, this.orderFieldsMapper.destinationToSource(value)))
        .orElseGet(() -> GenericUtils.buildGenericResponseError(StringUtils.EMPTY, null));
  }

  @Override
  public GenericResponse<List<OrderResponseDto>> getOrdersByCustomerId(final Integer customerId) {
    final List<Order> orders = this.orderRepository.findByCustomerId(customerId);
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.orderFieldsMapper.toDto(orders));
  }
}