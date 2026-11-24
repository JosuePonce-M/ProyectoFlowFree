package jp.flowfree.juego;


public class Nivel3 extends Nivel {

    public Nivel3() {
        super(3, 7, 55, 0);
        agregarPareja(ColorPunto.ROJO, 0, 2, 2, 0);
        agregarPareja(ColorPunto.VERDE_AGUA, 0, 3, 5, 1);
        agregarPareja(ColorPunto.ROSADO, 0, 6, 1, 4);
        agregarPareja(ColorPunto.MORADO, 1, 5, 4, 5);
        agregarPareja(ColorPunto.AZUL, 2, 2, 3, 0);
        agregarPareja(ColorPunto.AMARILLO, 3, 4, 6, 6);
        agregarPareja(ColorPunto.VERDE, 3, 5, 6, 5);
    }

    @Override
    public String getDescripcion() {
        return "Nuevo: tiempo límite de 55 segundos.";
    }
}
