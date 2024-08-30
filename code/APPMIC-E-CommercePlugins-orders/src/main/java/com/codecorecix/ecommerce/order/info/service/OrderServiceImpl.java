package com.codecorecix.ecommerce.order.info.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.codecorecix.ecommerce.event.clients.MaintenanceClientRest;
import com.codecorecix.ecommerce.event.entities.Order;
import com.codecorecix.ecommerce.event.entities.OrderDetail;
import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.order.info.api.dto.request.OrderRequestDto;
import com.codecorecix.ecommerce.order.info.api.dto.response.OrderResponseDto;
import com.codecorecix.ecommerce.order.info.mapper.OrderFieldsMapper;
import com.codecorecix.ecommerce.order.info.repository.OrderRepository;
import com.codecorecix.ecommerce.order.info.utils.OrderConstants;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
      this.validProduct(orderInfo);
      final Order order = this.orderRepository.save(orderInfo);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
          this.orderFieldsMapper.destinationToSource(order));
    } catch (Exception e) {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, GenericResponseConstants.WRONG_OPERATION,
          this.orderFieldsMapper.destinationToSource(this.orderFieldsMapper.sourceToDestination(orderRequestDto)));
    }
  }

  /**
   * Méthod for valid that the product id exist in database.
   * <p>First you will make asynchronous requests to validate products. In case there is a communication problem with the maintenance
   * microservice, exceptions are being handled.</p>
   * <p>Second we will wait for all the requests to finish and update the order details, and finally we map the products to the order
   * details.</p>
   *
   * @param orderInfo the request order.
   */
  private void validProduct(final Order orderInfo) {
    List<CompletableFuture<ProductResponseDto>> productFutures = orderInfo.getOrderDetails().stream()
        .map(orderDetail -> CompletableFuture.supplyAsync(() -> {
          try {
            ResponseEntity<GenericResponse<ProductResponseDto>> response =
                maintenanceClientRest.getProductById(orderDetail.getProductId());
            return Objects.requireNonNull(response.getBody()).getBody();
          } catch (final Exception e) {
            throw new RuntimeException(OrderConstants.NO_EXIST_PRODUCT_IN_BD + orderDetail.getProductId(), e);
          }
        }))
        .toList();
    CompletableFuture.allOf(productFutures.toArray(new CompletableFuture[0])).join();
    List<ProductResponseDto> products = productFutures.stream()
        .map(CompletableFuture::join)
        .toList();
    for (OrderDetail detail : orderInfo.getOrderDetails()) {
      ProductResponseDto product = products.stream()
          .filter(p -> p.getId().equals(detail.getProductId()))
          .findFirst()
          .orElseThrow(() -> new RuntimeException(OrderConstants.NO_EXIST_PRODUCT_ID + detail.getProductId()));
      detail.setProductId(product.getId());
    }
  }
}
