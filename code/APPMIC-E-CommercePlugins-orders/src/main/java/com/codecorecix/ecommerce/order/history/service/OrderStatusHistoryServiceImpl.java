package com.codecorecix.ecommerce.order.history.service;

import java.util.List;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.entities.OrderStatusHistory;
import com.codecorecix.ecommerce.order.history.api.dto.request.OrderStatusHistoryRequestDto;
import com.codecorecix.ecommerce.order.history.api.dto.response.OrderStatusHistoryResponseDto;
import com.codecorecix.ecommerce.order.history.mapper.OrderStatusHistoryFieldsMapper;
import com.codecorecix.ecommerce.order.history.repository.OrderStatusHistoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {

  private final OrderStatusHistoryRepository repository;

  private final OrderStatusHistoryFieldsMapper mapper;

  @Override
  public List<OrderStatusHistoryResponseDto> retrieveHistoryByOrderId(final Integer orderId) {
    final List<OrderStatusHistory> orderStatusHistories = this.repository.findByOrderIdOrderByChangedAtAsc(orderId);
    return orderStatusHistories
        .stream()
        .map(this.mapper::destinationToSource)
        .toList();
  }

  @Override
  public void saveOrderStatusHistory(final OrderStatusHistoryRequestDto orderStatusHistory) {
    final OrderStatusHistory orderStatusRepository = this.mapper.sourceToDestination(orderStatusHistory);
    this.mapper.destinationToSource(this.repository.save(orderStatusRepository));
  }
}
