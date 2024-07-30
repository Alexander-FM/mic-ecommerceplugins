package com.codecorecix.ecommerce.utils.solidprincipios.sustitucionliskov;

/**
 * Hereda de SuperTipoFigura y sobrescribe los métodos dibujar() y calcularArea() para implementar el comportamiento específico de un
 * rectángulo.
 */
public class SubTipoRectangulo extends SuperTipoFigura {

  private final Double ancho;

  private final Double alto;

  public SubTipoRectangulo(Double ancho, Double alto) {
    this.ancho = ancho;
    this.alto = alto;
  }

  @Override
  public void dibujar() {
    // Dibuja un rectángulo con asteriscos en la consola
    for (int i = 0; i < alto; i++) {
      for (int j = 0; j < ancho; j++) {
        System.out.print("*");
      }
      System.out.println();
    }
  }

  @Override
  public double calcularArea() {
    return ancho * alto;
  }
}
