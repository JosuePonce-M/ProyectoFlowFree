package jp.flowfree.juego;


public class Nivel4 extends Nivel {

    public Nivel4() {
        super(4, 8, 70, 0);
        agregarPareja(ColorPunto.ROJO, 5, 6, 7, 6);
        agregarPareja(ColorPunto.AZUL, 0, 1, 7, 7);
        agregarPareja(ColorPunto.VERDE, 0, 0, 7, 4);
        agregarPareja(ColorPunto.AMARILLO, 5, 2, 5, 5);
        agregarPareja(ColorPunto.MORADO, 5, 1, 6, 2);
        agregarPareja(ColorPunto.ROSADO, 3, 6, 4, 1);
        agregarPareja(ColorPunto.VERDE_AGUA, 2, 2, 3, 5);
        agregarPareja(ColorPunto.NARANJA, 1, 6, 3, 2);

        agregarPared(1, 5);
        agregarPared(6, 5);
        agregarPared(7, 5);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: paredes por las que no se puede pasar.";
    }
}
