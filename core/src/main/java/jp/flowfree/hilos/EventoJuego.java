package jp.flowfree.hilos;

import jp.flowfree.enums.TipoEvento;


public class EventoJuego {

    private final TipoEvento tipo;
    private final int fila;
    private final int columna;

    public EventoJuego(TipoEvento tipo, int fila, int columna) {
        this.tipo = tipo;
        this.fila = fila;
        this.columna = columna;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
