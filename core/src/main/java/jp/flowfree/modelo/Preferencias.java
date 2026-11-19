package jp.flowfree.modelo;

import java.io.Serializable;


public class Preferencias implements Serializable {

    private static final long serialVersionUID = 1L;

    private float volumenMusica;   
    private float volumenEfectos;  
    private boolean silencio;      
    private boolean musicaActiva;  
    private int pistaMenu;         
    private String idioma;        

    // Valores por defecto de un usuario nuevo
    public Preferencias() {
        this.volumenMusica = 0.7f;
        this.volumenEfectos = 0.8f;
        this.silencio = false;
        this.musicaActiva = true;
        this.pistaMenu = 1;
        this.idioma = "es";
    }

    public float getVolumenMusica() {
        return volumenMusica;
    }

    public void setVolumenMusica(float volumenMusica) {
        this.volumenMusica = volumenMusica;
    }

    public float getVolumenEfectos() {
        return volumenEfectos;
    }

    public void setVolumenEfectos(float volumenEfectos) {
        this.volumenEfectos = volumenEfectos;
    }

    public boolean isSilencio() {
        return silencio;
    }

    public void setSilencio(boolean silencio) {
        this.silencio = silencio;
    }

    public boolean isMusicaActiva() {
        return musicaActiva;
    }

    public void setMusicaActiva(boolean musicaActiva) {
        this.musicaActiva = musicaActiva;
    }

    public int getPistaMenu() {
        return pistaMenu;
    }

    public void setPistaMenu(int pistaMenu) {
        this.pistaMenu = pistaMenu;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
