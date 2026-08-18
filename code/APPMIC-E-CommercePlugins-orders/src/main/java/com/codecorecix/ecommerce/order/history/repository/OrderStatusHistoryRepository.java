package com.codecorecix.ecommerce.order.history.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.OrderStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {

  @Query("SELECT h FROM OrderStatusHistory h " +
      "JOIN FETCH h.order " +
      "JOIN FETCH h.orderStatus " +
      "WHERE h.order.id = :orderId " +
      "ORDER BY h.changedAt ASC")
  List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(@Param("orderId") final Integer orderId);

}
