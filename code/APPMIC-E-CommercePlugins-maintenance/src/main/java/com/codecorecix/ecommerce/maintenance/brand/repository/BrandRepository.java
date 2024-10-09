package com.codecorecix.ecommerce.maintenance.brand.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

  List<Brand> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Brand B SET B.isActive = ?1 WHERE B.id = ?2")
  void disabledOrEnabledBrand(final Boolean isActive, final Integer id);
}
