package jp.flowfree.modelo;

import java.io.Serializable;
import jp.flowfree.util.SeguridadContra;

/**
 * Datos del perfil de un jugador. Se guarda en el archivo perfil.dat de su carpeta.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String nombreCompleto;
    private String contrasenaProtegida; 
    private String sal;
    private long fechaRegistro;        
    private long ultimaSesion;         
    private String avatar;
    private int puntajeGeneral;
    private long tiempoTotalJugado;     

    public Usuario(String nombreUsuario, String nombreCompleto, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.sal = SeguridadContra.generarSal();
        this.contrasenaProtegida = SeguridadContra.protegerContrasena(contrasena, sal);
        this.fechaRegistro = System.currentTimeMillis();
        this.ultimaSesion = 0;
        this.avatar = "avatar1";
        this.puntajeGeneral = 0;
        this.tiempoTotalJugado = 0;
    }

    /** Revisa si la contraseña escrita es la correcta, sin guardar nunca la real. */
    public boolean verificarContrasena(String contrasena) {
        String codigo = SeguridadContra.protegerContrasena(contrasena, sal);
        return codigo.equals(contrasenaProtegida);
    }

    /** Cambia la contraseña (se usará en la pantalla de configuración). */
    public void cambiarContrasena(String nueva) {
        this.sal = SeguridadContra.generarSal();
        this.contrasenaProtegida = SeguridadContra.protegerContrasena(nueva, sal);
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public long getFechaRegistro() {
        return fechaRegistro;
    }

    public long getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(long ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPuntajeGeneral() {
        return puntajeGeneral;
    }

    public void setPuntajeGeneral(int puntajeGeneral) {
        this.puntajeGeneral = puntajeGeneral;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public void setTiempoTotalJugado(long tiempoTotalJugado) {
        this.tiempoTotalJugado = tiempoTotalJugado;
    }
}
