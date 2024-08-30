package com.codecorecix.ecommerce.order.status.repository;

import com.codecorecix.ecommerce.event.entities.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

}
