
package jp.flowfree.datos;

import java.io.File;
import java.io.IOException;
import jp.flowfree.excepciones.CamposVaciosException;
import jp.flowfree.excepciones.ContrasenaIncorrectaException;
import jp.flowfree.excepciones.ContrasenaInvalidaException;
import jp.flowfree.excepciones.UsuarioDuplicadoException;
import jp.flowfree.excepciones.UsuarioInvalidoException;
import jp.flowfree.excepciones.UsuarioNoExisteException;
import jp.flowfree.modelo.Estadisticas;
import jp.flowfree.modelo.Preferencias;
import jp.flowfree.modelo.Usuario;
import jp.flowfree.util.ValidarContrasena;
import jp.flowfree.modelo.Partida;

public class GestionUsuarios {
  private GestorArchivos archivos = new GestorArchivos();

 
    private Usuario usuarioActual;
    private Preferencias preferencias;
    private Estadisticas estadisticas;

    // Crea una cuenta nueva
    public void registrar(String nombreUsuario, String nombreCompleto, String contrasena, String confirmacion)
            throws CamposVaciosException, UsuarioInvalidoException, UsuarioDuplicadoException,
            ContrasenaInvalidaException, IOException {

        //  Ningún campo puede estar vacío
        if (nombreUsuario.isEmpty() || nombreCompleto.trim().isEmpty()
                || contrasena.isEmpty() || confirmacion.isEmpty()) {
            throw new CamposVaciosException();
        }
        // cuando un nombre es invalido
        if (usuarioValido(nombreUsuario) == false) {
            throw new UsuarioInvalidoException();
        }
        // El usuario no puede existir ya
        if (archivos.existeUsuario(nombreUsuario)) {
            throw new UsuarioDuplicadoException();
        }
        //  La contraseña debe cumplir los 5 requisitos
        if (ValidarContrasena.esValida(contrasena) == false) {
            throw new ContrasenaInvalidaException("La contraseña no cumple todos los requisitos.");
        }
   
        if (contrasena.equals(confirmacion) == false) {
            throw new ContrasenaInvalidaException("Las contraseñas no coinciden.");
        }

        // se crea la carpeta y sus archivos
        archivos.crearCarpetaUsuario(nombreUsuario);
        File carpeta = archivos.carpetaDe(nombreUsuario);

        Usuario nuevo = new Usuario(nombreUsuario, nombreCompleto.trim(), contrasena);
        archivos.guardar(new File(carpeta, GestorArchivos.Perfil), nuevo);
        archivos.guardar(new File(carpeta, GestorArchivos.Preferencias), new Preferencias());
        archivos.guardar(new File(carpeta, GestorArchivos.Estadisticas), new Estadisticas());
    }

   
    public Usuario iniciarSesion(String nombreUsuario, String contrasena)
            throws CamposVaciosException, UsuarioNoExisteException, ContrasenaIncorrectaException, IOException {

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            throw new CamposVaciosException();
        }

        File carpeta = archivos.carpetaDe(nombreUsuario);
        File archivoPerfil = new File(carpeta, GestorArchivos.Perfil);
        if (archivoPerfil.exists() == false) {
            throw new UsuarioNoExisteException();
        }

        Usuario usuario = (Usuario) archivos.leer(archivoPerfil);
        if (usuario.verificarContrasena(contrasena) == false) {
            throw new ContrasenaIncorrectaException();
        }

        // Contraseña correcta
        usuarioActual = usuario;
        preferencias = (Preferencias) archivos.leer(new File(carpeta, GestorArchivos.Preferencias));
        estadisticas = (Estadisticas) archivos.leer(new File(carpeta, GestorArchivos.Estadisticas));

   
        usuarioActual.setUltimaSesion(System.currentTimeMillis());
        archivos.guardar(archivoPerfil, usuarioActual);

        return usuarioActual;
    }

   
    public void cerrarSesion() throws IOException {
        if (usuarioActual == null) {
            return; 
        }
        File carpeta = archivos.carpetaDe(usuarioActual.getNombreUsuario());
        archivos.guardar(new File(carpeta, GestorArchivos.Perfil), usuarioActual);
        archivos.guardar(new File(carpeta, GestorArchivos.Preferencias), preferencias);
        archivos.guardar(new File(carpeta, GestorArchivos.Estadisticas), estadisticas);

        usuarioActual = null;
        preferencias = null;
        estadisticas = null;
    }

   
    private boolean usuarioValido(String nombreUsuario) {
        for (int i = 0; i < nombreUsuario.length(); i++) {
            char c = nombreUsuario.charAt(i);
            boolean letra = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            boolean numero = c >= '0' && c <= '9';
            boolean permitido = letra || numero || c == '_' || c == '-';
            if (permitido == false) {
                return false;
            }
        }
        return true;
    }

   
    public Partida leerPartida(int numero) throws IOException {
        return archivos.leerPartida(usuarioActual.getNombreUsuario(), numero);
    }

    public void guardarPartida(Partida partida) throws IOException {
        archivos.guardarPartida(usuarioActual.getNombreUsuario(), partida);
    }

    /** Borra la partida y recorre las siguientes un lugar hacia arriba para no dejar huecos */
    public void borrarPartida(int numero) throws IOException {
        String usuario = usuarioActual.getNombreUsuario();
        archivos.borrarPartida(usuario, numero);

        for (int siguiente = numero + 1; siguiente <= 3; siguiente++) {
            Partida partida = archivos.leerPartida(usuario, siguiente);
            if (partida == null) {
                continue;
            }
            partida.setNumero(siguiente - 1);          // la 3 pasa a ser la 2, etc.
            archivos.guardarPartida(usuario, partida);  // se guarda en partida2.dat
            archivos.borrarPartida(usuario, siguiente); // y se borra partida3.dat
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Preferencias getPreferencias() {
        return preferencias;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
  
}
