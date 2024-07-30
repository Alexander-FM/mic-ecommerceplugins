package com.codecorecix.ecommerce.utils.solidprincipios.responsabilidaunicayaperturacierre;

public class ApplicationMain {

  public static void main(String[] args) {
    // Crear un objeto de la clase Libro
    Libro libro = new Libro("El Quijote", 50.0, "Miguel de Cervantes", 1.5);

    // Llamar a métodos de la clase Libro
    libro.mostrarDetalles();
    System.out.println("Costo de envío: $" + libro.calcularEnvio());
    System.out.println("Costo de envío: $" + libro.calcularCostoEnvioDefault());
  }
}

