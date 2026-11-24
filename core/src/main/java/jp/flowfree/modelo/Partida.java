package jp.flowfree.modelo;

import java.io.Serializable;
import jp.flowfree.juego.EstadoNivel;

public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TOTAL_NIVELES = 5;
    public static final int VIDAS_INICIALES = 3;

    private int numero;                 
    private int vidas;
    private EstadoNivel[] estados;     
    private int[] puntajes;             
    private long tiempoJugado;         
    private int ultimoNivelGanado;     

    public Partida(int numero) {
        this.numero = numero;
        this.tiempoJugado = 0;
        reiniciar();
    }

  
    public void reiniciar() {
        vidas = VIDAS_INICIALES;
        estados = new EstadoNivel[TOTAL_NIVELES];
        puntajes = new int[TOTAL_NIVELES];
        for (int i = 0; i < TOTAL_NIVELES; i++) {
            estados[i] = EstadoNivel.BLOQUEADO;
            puntajes[i] = 0;
        }
        estados[0] = EstadoNivel.DISPONIBLE;
        ultimoNivelGanado = 0;
    }

  
    public void completarNivel(int nivel, int puntaje) {
        int i = nivel - 1;
        estados[i] = EstadoNivel.COMPLETADO;
        if (puntaje > puntajes[i]) {
            puntajes[i] = puntaje;
        }
        if (nivel < TOTAL_NIVELES) {
            if (estados[i + 1] == EstadoNivel.BLOQUEADO) {
                estados[i + 1] = EstadoNivel.DISPONIBLE;
            }
        }
        ultimoNivelGanado = nivel;
    }

   
    public boolean perderVida() {
        vidas--;
        if (vidas <= 0) {
            reiniciar();
            return true;
        }
        return false;
    }

    public boolean estaBloqueado(int nivel) {
        return estados[nivel - 1] == EstadoNivel.BLOQUEADO;
    }

    public int getPuntajeTotal() {
        int total = 0;
        for (int i = 0; i < TOTAL_NIVELES; i++) {
            total = total + puntajes[i];
        }
        return total;
    }

    public int getNivelesCompletados() {
        int cantidad = 0;
        for (int i = 0; i < TOTAL_NIVELES; i++) {
            if (estados[i] == EstadoNivel.COMPLETADO) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public void sumarTiempo(long segundos) {
        tiempoJugado = tiempoJugado + segundos;
    }

    // El mapa la lee para saber qué línea animar y luego la limpia,
    // así la animación sale una sola vez
    public void limpiarUltimoNivelGanado() {
        ultimoNivelGanado = 0;
    }

    public int getNumero() {
        return numero;
    }

    public int getVidas() {
        return vidas;
    }

    public EstadoNivel getEstado(int nivel) {
        return estados[nivel - 1];
    }

    public int getPuntaje(int nivel) {
        return puntajes[nivel - 1];
    }

    public long getTiempoJugado() {
        return tiempoJugado;
    }

    public int getUltimoNivelGanado() {
        return ultimoNivelGanado;
    }
}
