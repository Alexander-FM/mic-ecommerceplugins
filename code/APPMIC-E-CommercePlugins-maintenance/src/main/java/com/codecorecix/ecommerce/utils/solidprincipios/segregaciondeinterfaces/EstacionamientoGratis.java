package com.codecorecix.ecommerce.utils.solidprincipios.segregaciondeinterfaces;

public interface EstacionamientoGratis extends Estacionamiento {

  @Override
  void aparcarCoche();

  @Override
  void sacarCoche();

  @Override
  Integer getCapacidad();
}
