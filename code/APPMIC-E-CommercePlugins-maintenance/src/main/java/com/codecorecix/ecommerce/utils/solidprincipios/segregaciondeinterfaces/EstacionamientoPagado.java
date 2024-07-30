package com.codecorecix.ecommerce.utils.solidprincipios.segregaciondeinterfaces;

public interface EstacionamientoPagado extends Estacionamiento {

  @Override
  void aparcarCoche();

  @Override
  void sacarCoche();

  @Override
  Integer getCapacidad();

  Double calcularTarifa(final Integer cantidadHoras);

  void hacerPago();
}
