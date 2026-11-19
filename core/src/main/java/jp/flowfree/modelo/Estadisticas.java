package jp.flowfree.modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Estadisticas implements Serializable {

    private static final long serialVersionUID = 1L;

    private int partidasJugadas;
    private int nivelesCompletados;
    private long tiempoTotalJugado;        
    private ArrayList<String> historial;   

   
    public Estadisticas() {
        this.partidasJugadas = 0;
        this.nivelesCompletados = 0;
        this.tiempoTotalJugado = 0;
        this.historial = new ArrayList<>();
    }

    
    public void agregarAlHistorial(String registro) {
        historial.add(registro);
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getNivelesCompletados() {
        return nivelesCompletados;
    }

    public void setNivelesCompletados(int nivelesCompletados) {
        this.nivelesCompletados = nivelesCompletados;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public void setTiempoTotalJugado(long tiempoTotalJugado) {
        this.tiempoTotalJugado = tiempoTotalJugado;
    }

    public ArrayList<String> getHistorial() {
        return historial;
    }
}