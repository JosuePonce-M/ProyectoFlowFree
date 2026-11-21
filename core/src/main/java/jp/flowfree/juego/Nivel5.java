package jp.flowfree.juego;


public class Nivel5 extends Nivel {

    public Nivel5() {
        super(5, 9, 65, 3);
        agregarPareja(ColorPunto.ROJO, 2, 3, 3, 7);
        agregarPareja(ColorPunto.AZUL, 2, 2, 8, 8);
        agregarPareja(ColorPunto.VERDE, 7, 0, 8, 7);
        agregarPareja(ColorPunto.AMARILLO, 6, 0, 7, 2);
        agregarPareja(ColorPunto.MORADO, 0, 6, 6, 2);
        agregarPareja(ColorPunto.ROSADO, 0, 8, 1, 3);
        agregarPareja(ColorPunto.VERDE_AGUA, 1, 2, 6, 4);
        agregarPareja(ColorPunto.NARANJA, 6, 5, 7, 7);
        agregarPareja(ColorPunto.AZUL_OSCURO, 5, 3, 7, 6);

        agregarPared(0, 7);
        agregarPared(2, 7);
        agregarPared(2, 8);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: solo 3 fallos permitidos.";
    }
}
