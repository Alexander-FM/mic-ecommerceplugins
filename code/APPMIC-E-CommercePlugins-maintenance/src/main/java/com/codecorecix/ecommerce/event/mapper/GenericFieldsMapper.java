package com.codecorecix.ecommerce.event.mapper;

import java.util.List;

public interface GenericFieldsMapper<D, E> {

  /**
   * To entity.
   *
   * @param dtoList the dto list
   * @return the list
   */
  List<E> toEntity(List<D> dtoList);

  /**
   * To dto.
   *
   * @param entityList the entity list
   * @return the list
   */
  List<D> toDto(List<E> entityList);
}
