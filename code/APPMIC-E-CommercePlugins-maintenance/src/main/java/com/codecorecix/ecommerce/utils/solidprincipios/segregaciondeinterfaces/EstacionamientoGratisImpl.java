package com.codecorecix.ecommerce.utils.solidprincipios.segregaciondeinterfaces;

public class EstacionamientoGratisImpl implements EstacionamientoGratis {

  private Integer capacidad;

  public EstacionamientoGratisImpl(Integer capacidad) {
    this.capacidad = capacidad;
  }

  @Override
  public void aparcarCoche() {
    capacidad--;
  }

  @Override
  public void sacarCoche() {
    capacidad++;
  }

  @Override
  public Integer getCapacidad() {
    return capacidad;
  }
}
