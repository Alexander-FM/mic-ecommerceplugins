package com.codecorecix.ecommerce.maintenance.product.detail.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.ProductDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

  @Query("SELECT PD FROM ProductDetail PD WHERE PD.product.id = :id")
  List<ProductDetail> findAllDetailByIdProduct(final Integer id);
}
