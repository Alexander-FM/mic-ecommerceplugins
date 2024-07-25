package com.codecorecix.ecommerce.utils.solidprincipios.segregaciondeinterfaces;

public class EstacionamientoPagoPorHorasImpl implements EstacionamientoPagoPorHora {

  private Integer capacidad;

  private final Double tarifaPorHora;

  public EstacionamientoPagoPorHorasImpl(int capacidad, double tarifaPorHora) {
    this.capacidad = capacidad;
    this.tarifaPorHora = tarifaPorHora;
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

  @Override
  public Double calcularTarifa(final Integer cantidadHoras) {
    return tarifaPorHora * cantidadHoras;
  }

  @Override
  public void hacerPago() {
    // Lógica para procesar el pago
  }
}
