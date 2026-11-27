package jp.flowfree.juego;

import jp.flowfree.enums.ColorPunto;

public class Nivel1 extends Nivel {

    public Nivel1() {
        super(1, 5, 0, 0);
        agregarPareja(ColorPunto.VERDE, 1, 0, 4, 0);
        agregarPareja(ColorPunto.ROJO, 1, 1, 3, 1);
        agregarPareja(ColorPunto.AMARILLO, 2, 2, 4, 1);
        agregarPareja(ColorPunto.AZUL, 1, 2, 4, 3);
        agregarPareja(ColorPunto.MORADO, 0, 0, 4, 4);
    }

    @Override
    public String getDescripcion() {
        return "Une cada pareja de colores y llena todo el tablero.";
    }
}
