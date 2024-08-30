package com.codecorecix.ecommerce.order.info.repository;

import com.codecorecix.ecommerce.event.entities.OrderDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

}
