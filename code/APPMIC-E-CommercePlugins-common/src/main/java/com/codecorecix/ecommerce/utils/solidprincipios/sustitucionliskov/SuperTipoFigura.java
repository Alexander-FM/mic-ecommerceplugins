package com.codecorecix.ecommerce.utils.solidprincipios.sustitucionliskov;

/**
 * Define métodos dibujar() y calcularArea(), que son los comportamientos generales que se espera que cualquier figura geométrica pueda
 * realizar.
 */
public class SuperTipoFigura {

  public void dibujar() {
    System.out.println("Dibujando una figura genérica");
  }

  public double calcularArea() {
    return 0.0;
  }
}
