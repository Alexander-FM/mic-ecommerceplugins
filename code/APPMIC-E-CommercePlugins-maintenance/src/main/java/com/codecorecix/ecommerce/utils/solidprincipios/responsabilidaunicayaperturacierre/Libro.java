package com.codecorecix.ecommerce.utils.solidprincipios.responsabilidaunicayaperturacierre;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Libro extends Producto implements Enviable {

  private String autor;

  private Double peso; // Supongamos que el peso es necesario para calcular el costo de envío.

  public Libro(String nombre, Double precio, String autor, Double peso) {
    super(nombre, precio);
    this.autor = autor;
    this.peso = peso;
  }

  @Override
  public void mostrarDetalles() {
    System.out.println("Este libro se llama: " + getNombre() + ", y es del autor: " + getAutor() + " Su precio es $ " + getPrecio());
  }

  @Override
  public Double calcularCostoEnvioDefault() {
    return Enviable.super.calcularCostoEnvioDefault();
  }

  @Override
  public Double calcularEnvio() {
    return peso * 0.69;
  }
}
