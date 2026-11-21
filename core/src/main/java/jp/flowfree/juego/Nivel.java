package jp.flowfree.juego;

import java.util.ArrayList;

public class Nivel {

    protected int numero;
    protected int tamano;         
    protected int tiempoLimite;     
    protected int fallosPermitidos;   
    protected ArrayList<Pareja> parejas = new ArrayList<>();
    protected ArrayList<int[]> paredes = new ArrayList<>(); 

    public Nivel(int numero, int tamano, int tiempoLimite, int fallosPermitidos) {
        this.numero = numero;
        this.tamano = tamano;
        this.tiempoLimite = tiempoLimite;
        this.fallosPermitidos = fallosPermitidos;
    }

    /** Agrega una pareja de puntos del mismo color. */
    protected void agregarPareja(ColorPunto color, int fila1, int columna1, int fila2, int columna2) {
        parejas.add(new Pareja(color, fila1, columna1, fila2, columna2));
    }

    /** Agrega una casilla bloqueada. */
    protected void agregarPared(int fila, int columna) {
        paredes.add(new int[]{fila, columna});
    }

    /** Texto que explica lo nuevo de este nivel */
    public String getDescripcion() {
        return "Conecta todas las parejas y llena el tablero.";
    }

    public boolean tieneTiempoLimite() {
        return tiempoLimite > 0;
    }

    public boolean tieneLimiteFallos() {
        return fallosPermitidos > 0;
    }

    public int getNumero() {
        return numero;
    }

    public int getTamano() {
        return tamano;
    }

    public int getTiempoLimite() {
        return tiempoLimite;
    }

    public int getFallosPermitidos() {
        return fallosPermitidos;
    }

    public ArrayList<Pareja> getParejas() {
        return parejas;
    }

    public ArrayList<int[]> getParedes() {
        return paredes;
    }
}
