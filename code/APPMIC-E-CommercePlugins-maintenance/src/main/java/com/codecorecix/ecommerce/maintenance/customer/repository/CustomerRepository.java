package com.codecorecix.ecommerce.maintenance.customer.repository;

import com.codecorecix.ecommerce.event.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

  @Modifying
  @Query("UPDATE Customer C SET C.isActive = ?1 WHERE C.id = ?2")
  void disabledOrEnabledCustomer(final Boolean isActive, final Integer id);

  /**
   * Busca si el userId ya está asociado a otro cliente distinto al Id proporcionando.
   *
   * @param userId id del usuario a buscar
   * @param id id del cliente a excluir de la búsqueda
   * @return true si existe otro cliente con el mismo userId, false en caso contrario
   */
  boolean existByUserIdAndIdNot(Integer userId, int id);
}
