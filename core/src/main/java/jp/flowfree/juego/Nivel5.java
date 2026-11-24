package jp.flowfree.juego;


public class Nivel5 extends Nivel {

    public Nivel5() {
        super(5, 9, 45, 3);
        agregarPareja(ColorPunto.MORADO, 0, 4, 3, 0);
        agregarPareja(ColorPunto.ROSADO, 0, 5, 0, 8);
        agregarPareja(ColorPunto.VERDE_AGUA, 1, 5, 4, 4);
        agregarPareja(ColorPunto.ROJO, 2, 8, 3, 2);
        agregarPareja(ColorPunto.AZUL, 3, 3, 8, 8);
        agregarPareja(ColorPunto.AMARILLO, 4, 0, 7, 1);
        agregarPareja(ColorPunto.AZUL_OSCURO, 6, 3, 7, 6);
        agregarPareja(ColorPunto.NARANJA, 6, 5, 7, 2);
        agregarPareja(ColorPunto.VERDE, 6, 7, 7, 0);

        agregarPared(0, 7);
        agregarPared(2, 7);
        agregarPared(3, 7);
        agregarPared(5, 1);
        agregarPared(6, 6);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: solo 3 fallos permitidos.";
    }
}
