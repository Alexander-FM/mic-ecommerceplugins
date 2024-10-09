package com.codecorecix.ecommerce.order.status.service;

import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.OrderStatus;
import com.codecorecix.ecommerce.order.status.api.dto.request.OrderStatusRequestDto;
import com.codecorecix.ecommerce.order.status.api.dto.response.OrderStatusResponseDto;
import com.codecorecix.ecommerce.order.status.mapper.OrderStatusFieldsMapper;
import com.codecorecix.ecommerce.order.status.repository.OrderStatusRepository;
import com.codecorecix.ecommerce.order.status.utils.OrderStatusConstants;
import com.codecorecix.ecommerce.order.status.utils.OrderStatusUtils;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

  private final OrderStatusRepository repository;

  private final OrderStatusFieldsMapper mapper;

  @Override
  public GenericResponse<List<OrderStatusResponseDto>> listStatus() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.toDto(this.repository.findAll()));
  }

  @Override
  public GenericResponse<OrderStatusResponseDto> saveStatus(final OrderStatusRequestDto orderStatusRequestDto) {
    final OrderStatus orderStatus = (this.repository.save(this.mapper.sourceToDestination(orderStatusRequestDto)));
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.destinationToSource(orderStatus));
  }

  @Override
  public GenericResponse<OrderStatusResponseDto> deleteStatus(final Integer id) {
    final Optional<OrderStatus> status = this.repository.findById(id);
    if (status.isPresent()) {
      this.repository.deleteById(status.get().getId());
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, OrderStatusConstants.NO_EXIST),
          null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<OrderStatusResponseDto> disabledOrEnabledStatus(final Boolean isActive, final Integer id) {
    final Optional<OrderStatus> statusOptional = this.repository.findById(id);
    if (statusOptional.isPresent()) {
      this.repository.save(new OrderStatus(statusOptional.get().getId(), statusOptional.get().getStatusName(), isActive));
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, OrderStatusConstants.NO_EXIST),
          null);
    }
  }

  @Override
  public GenericResponse<OrderStatusResponseDto> findById(final Integer id) {
    final Optional<OrderStatus> orderStatus = this.repository.findById(id);
    return orderStatus.map(value -> OrderStatusUtils.buildGenericResponse(this.mapper.destinationToSource(value)))
        .orElseGet(OrderStatusUtils::buildGenericResponseError);
  }
}
