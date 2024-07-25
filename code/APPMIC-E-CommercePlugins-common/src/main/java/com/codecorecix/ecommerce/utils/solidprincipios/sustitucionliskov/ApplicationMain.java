package com.codecorecix.ecommerce.utils.solidprincipios.sustitucionliskov;

public class ApplicationMain {

  /**
   * En el método imprimirArea(), se espera un objeto de tipo Figura. Gracias a la herencia y al polimorfismo en Java, podemos pasar un
   * objeto Rectángulo sin problemas, ya que Rectángulo es un subtipo de Figura.
   */
  public static void imprimirArea(SuperTipoFigura figura) {
    double area = figura.calcularArea();
    System.out.println("El área de la figura es: " + area);
  }

  /**
   * El método main() crea un objeto Rectángulo y lo pasa a imprimirArea(). Esto demuestra que podemos usar un objeto de subtipo
   * (Rectángulo) en lugar de un objeto de SuperTipo (Figura) sin que el programa se comporte de manera incorrecta.
   */
  public static void main(String[] args) {
    SubTipoRectangulo miRectangulo = new SubTipoRectangulo(5.0, 3.0);
    imprimirArea(miRectangulo);

    // También podemos dibujar el rectángulo
    miRectangulo.dibujar();

    // Crear un cuadrado y tratar de usarlo como rectángulo
    SubTipoCuadrado miCuadrado = new SubTipoCuadrado(4.0);
    imprimirArea(miCuadrado);
    miCuadrado.dibujar(); // Aquí es donde se ve la violación potencial

    // Supongamos que queremos usar un array de Figuras
    SuperTipoFigura[] figuras = new SuperTipoFigura[3];
    figuras[0] = new SubTipoRectangulo(4.0, 2.0);
    figuras[1] = new SubTipoRectangulo(3.0, 3.0);
    figuras[2] = miCuadrado; // Aquí también podría haber problemas

    for (SuperTipoFigura figura : figuras) {
      imprimirArea(figura);
      figura.dibujar();
    }
  }
}
