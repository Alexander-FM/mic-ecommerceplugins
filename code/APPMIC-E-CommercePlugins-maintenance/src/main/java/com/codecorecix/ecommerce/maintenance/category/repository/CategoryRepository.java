package com.codecorecix.ecommerce.maintenance.category.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  @Query("SELECT C FROM Category C WHERE C.parentCategory = ?1")
  List<Category> findCategoryByParentCategory(final Integer parentCategory);

  @Query("SELECT C FROM Category C WHERE C.parentCategory = ?1 AND C.isActive = true")
  List<Category> findCategoryByParentCategoryAndIsActiveIsTrue(final Integer parentCategory);

  List<Category> findCategoryByParentCategoryIsNull();

  List<Category> findCategoryByParentCategoryIsNullAndIsActiveIsTrue();

  List<Category> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Category C SET C.isActive = ?1 WHERE C.id = ?2")
  void disabledOrEnabledCategory(final Boolean isActive, final Integer id);
}
