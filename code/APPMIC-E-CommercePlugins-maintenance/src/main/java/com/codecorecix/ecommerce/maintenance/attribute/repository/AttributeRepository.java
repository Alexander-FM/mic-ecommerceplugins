package com.codecorecix.ecommerce.maintenance.attribute.repository;

import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {

  Optional<Attribute> findByName(String name);
}

