package com.codecorecix.ecommerce.order.info.repository;

import com.codecorecix.ecommerce.event.entities.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
