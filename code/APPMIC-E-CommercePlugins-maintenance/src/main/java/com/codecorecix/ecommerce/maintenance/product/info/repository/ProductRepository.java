package com.codecorecix.ecommerce.maintenance.product.info.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  List<Product> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Product P SET P.isActive = ?1 WHERE P.id = ?2")
  void desactivateOrActivateProduct(final Boolean isActive, final Integer id);
}
