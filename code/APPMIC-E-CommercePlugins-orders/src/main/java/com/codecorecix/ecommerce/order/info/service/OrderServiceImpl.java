package com.codecorecix.ecommerce.order.info.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.exceptions.OrderException;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderRepository;
import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;
import com.codecorecix.ecommerce.order.status.service.OrderStatusService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import com.codecorecix.ecommerce.utils.GenericUtils;
import com.codecorecix.ecommerce.utils.OrderErrorMessage;

import feign.FeignException;
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

  private final OrderDetailService orderDetailService;

  @Override
  @Transactional
  public GenericResponse<OrderResponseDto> saveOrder(final OrderRequestDto orderRequestDto) {
    try {
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
      this.orderDetailService.saveOrderDetails(orderRequestDto.getOrderDetails(), orderBD.getId());
      final OrderResponseDto orderResponseDto = this.orderFieldsMapper.destinationToSource(orderBD);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, orderResponseDto);
    } catch (final FeignException.Unauthorized ex1) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_NOT_AUTHORIZED);
    } catch (final FeignException.Forbidden ex2) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_FORBIDDEN);
    } catch (final FeignException.NotFound notFound) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_NOT_FOUND);
    } catch (final FeignException.InternalServerError internalServerError) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_ENDPOINT_ERROR);
    } catch (final FeignException e) {
      throw new OrderException(OrderErrorMessage.SERVICE_PRODUCTS_NOT_AVAILABLE);
    } catch (final OrderException ex) {
      throw new OrderException(ex.getErrorMessage());
    }
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
}
