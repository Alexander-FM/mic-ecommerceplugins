package com.codecorecix.ecommerce.maintenance.role.repository;

import java.util.List;

import com.codecorecix.ecommerce.event.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

  List<Role> findByIsActiveIsTrue();

  @Modifying
  @Query("UPDATE Role R SET R.isActive = ?1 WHERE R.id = ?2")
  void disabledOrEnabledBrand(final Boolean isActive, final Integer id);
}
