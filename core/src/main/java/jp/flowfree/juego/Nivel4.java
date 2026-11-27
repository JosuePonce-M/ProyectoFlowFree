package jp.flowfree.juego;

import jp.flowfree.enums.ColorPunto;


public class Nivel4 extends Nivel {

    public Nivel4() {
        super(4, 8, 50, 0);
        agregarPareja(ColorPunto.AZUL, 4, 0, 4, 7);
        agregarPareja(ColorPunto.VERDE_AGUA, 2, 2, 4, 2);
        agregarPareja(ColorPunto.ROSADO, 2, 6, 3, 4);
        agregarPareja(ColorPunto.NARANJA, 3, 2, 3, 5);
        agregarPareja(ColorPunto.VERDE, 5, 1, 6, 3);
        agregarPareja(ColorPunto.MORADO, 5, 3, 6, 1);
        agregarPareja(ColorPunto.AMARILLO, 5, 5, 7, 5);
        agregarPareja(ColorPunto.ROJO, 5, 6, 7, 6);

        agregarPared(1, 5);
        agregarPared(1, 6);
        agregarPared(4, 1);
        agregarPared(6, 5);
        agregarPared(6, 6);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: paredes por las que no se puede pasar.";
    }
}
