package com.codecorecix.ecommerce.maintenance.customer.repository;

import com.codecorecix.ecommerce.event.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

  @Modifying
  @Query("UPDATE Customer C SET C.isActive = ?1 WHERE C.id = ?2")
  void disabledOrEnabledCustomer(final Boolean isActive, final Integer id);
}
