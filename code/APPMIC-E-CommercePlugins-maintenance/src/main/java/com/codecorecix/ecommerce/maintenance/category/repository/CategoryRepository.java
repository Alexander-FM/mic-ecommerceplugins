package com.codecorecix.ecommerce.maintenance.category.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  List<Category> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Category C SET C.isActive = ?1 WHERE C.id = ?2")
  void desactivateOrActivateCategory(final Boolean isActive, final Integer id);
}
