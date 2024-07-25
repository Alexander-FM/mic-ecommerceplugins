package com.codecorecix.ecommerce.utils.solidprincipios.responsabilidaunicayaperturacierre;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Producto {

  private String nombre;

  private Double precio;

  public abstract void mostrarDetalles();
}
