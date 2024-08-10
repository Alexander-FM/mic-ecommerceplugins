package com.codecorecix.ecommerce.maintenance.product.image.repository;

import com.codecorecix.ecommerce.event.entities.ProductImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

}
