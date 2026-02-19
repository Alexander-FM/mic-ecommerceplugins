package com.codecorecix.ecommerce.maintenance.employee.repository;

import com.codecorecix.ecommerce.event.entities.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  @Modifying
  @Query("UPDATE Employee E SET E.isActive = ?1 WHERE E.id = ?2")
  void disabledOrEnabledEmployee(final Boolean isActive, final Integer id);

  /**
   * Busca si el userId ya está asociado a otro empleado distinto al Id proporcionando.
   *
   * @param userId id del usuario a buscar
   * @param id id del empleado a excluir de la búsqueda
   * @return true si existe otro empleado con el mismo userId, false en caso contrario
   */
  boolean existByUserIdAndIdNot(Integer userId, int id);
}
