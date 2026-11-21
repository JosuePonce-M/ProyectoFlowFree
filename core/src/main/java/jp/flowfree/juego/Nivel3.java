package jp.flowfree.juego;


public class Nivel3 extends Nivel {

    public Nivel3() {
        super(3, 7, 75, 0);
        agregarPareja(ColorPunto.ROJO, 0, 6, 2, 6);
        agregarPareja(ColorPunto.AZUL, 2, 5, 4, 0);
        agregarPareja(ColorPunto.VERDE, 1, 2, 3, 0);
        agregarPareja(ColorPunto.AMARILLO, 1, 1, 5, 4);
        agregarPareja(ColorPunto.MORADO, 4, 2, 5, 5);
        agregarPareja(ColorPunto.ROSADO, 0, 4, 3, 2);
        agregarPareja(ColorPunto.VERDE_AGUA, 0, 5, 3, 3);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: tiempo límite de 75 segundos.";
    }
}
