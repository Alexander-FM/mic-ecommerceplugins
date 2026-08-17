package com.codecorecix.ecommerce.maintenance.product.info.repository;

import com.codecorecix.ecommerce.event.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  List<Product> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Product P SET P.isActive = ?1 WHERE P.id = ?2")
  void disabledOrEnabledProduct(final Boolean isActive, final Integer id);

  /**
   * Method used to find the product by id with all its attributes and images.
   *
   * @param id The id of the product.
   * @return Optional of Product.
   */
  @EntityGraph(attributePaths = {"attributes", "images"})
  @Query("SELECT p FROM Product p WHERE p.id = :id")
  Optional<Product> findByIdFull(final Integer id);

  List<Product> findByCategoryId(final Integer categoryId);
}