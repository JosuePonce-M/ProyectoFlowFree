package jp.flowfree.juego;


public class Pareja {

    private ColorPunto color;
    private int fila1;
    private int columna1;
    private int fila2;
    private int columna2;

    public Pareja(ColorPunto color, int fila1, int columna1, int fila2, int columna2) {
        this.color = color;
        this.fila1 = fila1;
        this.columna1 = columna1;
        this.fila2 = fila2;
        this.columna2 = columna2;
    }

    public ColorPunto getColor() {
        return color;
    }

    public int getFila1() {
        return fila1;
    }

    public int getColumna1() {
        return columna1;
    }

    public int getFila2() {
        return fila2;
    }

    public int getColumna2() {
        return columna2;
    }
}
