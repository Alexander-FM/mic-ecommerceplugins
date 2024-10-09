package com.codecorecix.ecommerce.maintenance.product.info.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Product;
import com.codecorecix.ecommerce.event.models.ProductInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  List<Product> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Product P SET P.isActive = ?1 WHERE P.id = ?2")
  void disabledOrEnabledProduct(final Boolean isActive, final Integer id);

  @Query("SELECT new com.codecorecix.ecommerce.event.models.ProductInfo("
      + "P.id, P.barCode, P.name, P.description, P.price, P.stock, "
      + "P.category.description, P.brand.description) "
      + "FROM Product P WHERE P.id IN :ids")
  List<ProductInfo> findByProductsByIds(@Param("ids") List<Integer> ids);
}
