package jp.flowfree.juego;


public class Nivel2 extends Nivel {

    public Nivel2() {
        super(2, 6, 0, 0);
        agregarPareja(ColorPunto.AMARILLO, 2, 3, 5, 3);
        agregarPareja(ColorPunto.VERDE, 3, 4, 4, 3);
        agregarPareja(ColorPunto.AZUL, 2, 2, 3, 3);
        agregarPareja(ColorPunto.ROJO, 0, 5, 2, 1);
        agregarPareja(ColorPunto.ROSADO, 0, 4, 4, 1);
        agregarPareja(ColorPunto.MORADO, 4, 0, 4, 2);
    }

    @Override
    public String getDescripcion() {
        return "Más colores en un tablero más grande.";
    }
}
