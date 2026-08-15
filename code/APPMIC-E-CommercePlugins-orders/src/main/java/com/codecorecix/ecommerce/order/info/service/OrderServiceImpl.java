package com.codecorecix.ecommerce.order.info.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.clients.MaintenanceClientRest;
import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.event.entities.OrderStatus;
import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.exceptions.OrderException;
import com.codecorecix.ecommerce.order.history.api.dto.request.OrderStatusHistoryRequestDto;
import com.codecorecix.ecommerce.order.history.service.OrderStatusHistoryService;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderDetailRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderStatusUpdateRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderDetailFieldsMapper;
import com.codecorecix.ecommerce.order.info.mapper.OrderFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderDetailRepository;
import com.codecorecix.ecommerce.order.info.repository.OrderRepository;
import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;
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

  private final OrderStatusHistoryService orderStatusHistoryService;

  private static final Map<Integer, Set<Integer>> validTransitions = new HashMap<>();

  static {
    validTransitions.put(1, Set.of(2, 6)); // Pendiente -> Recepcionado, Cancelado
    validTransitions.put(2, Set.of(3, 6)); // Recepcionado -> Preparando pedido, Cancelado
    validTransitions.put(3, Set.of(4, 6)); // Preparando pedido -> En camino, Cancelado
    validTransitions.put(4, Set.of(5));    // En camino -> Entregado
    validTransitions.put(5, Collections.emptySet()); // Entregado -> (Estado final)
    validTransitions.put(6, Collections.emptySet()); // Cancelado -> (Estado final)
  }

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

    Map<Integer, ProductInfo> productInfoMap = response
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

    final GenericResponse<OrderStatusResponseDto> findStatusById = this.orderStatusService.findById(1);

    if (findStatusById
        .getRpta()
        .equals(-1)) {
      throw new OrderException(OrderErrorMessage.ERROR_RESOURCE_STATUS_NOT_AVAILABLE);
    }

    OrderStatus orderStatus = new OrderStatus(findStatusById
        .getBody()
        .getId(), findStatusById
        .getBody()
        .getStatusName(), findStatusById
        .getBody()
        .getIsActive());
    orderInfo.setOrderStatus(orderStatus);

    final Order orderBD = this.orderRepository.save(orderInfo);

    final List<OrderDetail> orderDetails = new ArrayList<>();
    for (final OrderDetailRequestDto orderDetail : orderRequestDto.getOrderDetails()) {
      final OrderDetail orderDetailEntity = this.orderDetailFieldsMapper.toEntity(orderDetail, orderBD.getId());
      orderDetails.add(orderDetailEntity);
    }

    this.orderDetailRepository.saveAll(orderDetails);

    final OrderStatusHistoryRequestDto orderStatusHistoryRequestDto = new OrderStatusHistoryRequestDto();
    orderStatusHistoryRequestDto.setOrder(new OrderRequestDto(orderBD.getId()));
    orderStatusHistoryRequestDto.setOrderStatus(new OrderStatusRequestDto(orderBD
        .getOrderStatus()
        .getId()));
    orderStatusHistoryRequestDto.setChangedAt(LocalDateTime.now(ZoneId.systemDefault()));
    orderStatusHistoryRequestDto.setChangedBy("Sistema");
    orderStatusHistoryRequestDto.setObservation("Recibimos tu solicitud de compra, pronto un agente recepcionará tu pedido");

    this.orderStatusHistoryService.saveOrderStatusHistory(orderStatusHistoryRequestDto);

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

  @Override
  public GenericResponse<List<OrderResponseDto>> getAllOrders() {
    final List<Order> orders = this.orderRepository.findAll();
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.orderFieldsMapper.toDto(orders));
  }

  @Override
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

  @Override
  @Transactional
  public GenericResponse<OrderResponseDto> updateOrderStatus(final Long orderId, final OrderStatusUpdateRequestDto requestDto) {
    Order order = orderRepository
        .findById(Math.toIntExact(orderId))
        .orElseThrow(() -> new OrderException(OrderErrorMessage.ERROR_RESOURCE_ORDER_NOT_AVAILABLE));

    Integer currentStatusId = order
        .getOrderStatus()
        .getId();
    Integer newStatusId = requestDto.getNewStatusId();

    Set<Integer> allowedNextStates = validTransitions.getOrDefault(currentStatusId, Collections.emptySet());

    if (!allowedNextStates.contains(newStatusId)) {
      String currentStatusName = orderStatusService
          .findById(currentStatusId)
          .getBody()
          .getStatusName();
      String newStatusName = orderStatusService
          .findById(newStatusId)
          .getBody()
          .getStatusName();
      throw new OrderException(OrderErrorMessage.INVALID_STATUS_TRANSITION, currentStatusName, newStatusName);
    }

    OrderStatus newOrderStatus = new OrderStatus();
    newOrderStatus.setId(newStatusId);
    order.setOrderStatus(newOrderStatus);

    Order updatedOrder = orderRepository.save(order);

    // Almacenar el estado actual en el historial de la orden
    final OrderStatusHistoryRequestDto orderStatusHistoryRequestDto = new OrderStatusHistoryRequestDto();
    orderStatusHistoryRequestDto.setOrder(new OrderRequestDto(updatedOrder.getId()));
    orderStatusHistoryRequestDto.setOrderStatus(new OrderStatusRequestDto(updatedOrder
        .getOrderStatus()
        .getId()));
    orderStatusHistoryRequestDto.setObservation(requestDto.getObservation());
    orderStatusHistoryRequestDto.setChangedAt(LocalDateTime.now(ZoneId.systemDefault()));
    orderStatusHistoryRequestDto.setChangedBy(requestDto.getChangedBy());
    this.orderStatusHistoryService.saveOrderStatusHistory(orderStatusHistoryRequestDto);

    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        orderFieldsMapper.destinationToSource(updatedOrder));
  }
}