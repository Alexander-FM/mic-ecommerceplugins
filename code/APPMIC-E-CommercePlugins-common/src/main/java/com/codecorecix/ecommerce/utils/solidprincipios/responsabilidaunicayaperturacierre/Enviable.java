package com.codecorecix.ecommerce.utils.solidprincipios.responsabilidaunicayaperturacierre;

/**
 * Una interfaz en Java define un contrato que las clases pueden implementar. Es similar a una clase abstracta en el sentido de que puede
 * contener métodos abstractos, pero no puede tener campos ni implementaciones de métodos. Algunas características clave de las interfaces
 * son: -- Solo puede contener métodos abstractos y constantes (variables estáticas final). No puede tener constructores ni bloques de
 * inicialización. Una clase puede implementar múltiples interfaces.
 */
public interface Enviable {

  default Double calcularCostoEnvioDefault() {
    return 3.45;
  }

  Double calcularEnvio();
}
