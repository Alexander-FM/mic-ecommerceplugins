package com.codecorecix.ecommerce.category.repository;

import com.codecorecix.ecommerce.event.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  @Query("SELECT C FROM Category C WHERE C.isActive IS true")
  Iterable<Category> listActiveCategories();
}
