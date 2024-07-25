package com.codecorecix.ecommerce.utils.solidprincipios.sustitucionliskov;

/**
 * Violación de Liskov. Supongamos que tenemos una clase Cuadrado que extiende de Rectángulo. Según las propiedades matemáticas, un cuadrado
 * es un tipo especial de rectángulo donde los lados son iguale
 */
public class SubTipoCuadrado extends SubTipoRectangulo {

  public SubTipoCuadrado(Double lado) {
    super(lado, lado);
  }
}
