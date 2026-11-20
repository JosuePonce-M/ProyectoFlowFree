package jp.flowfree.datos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class GestorArchivos {

    public static final String Perfil = "perfil.dat";
    public static final String Preferencias = "preferencias.dat";
    public static final String Estadisticas = "estadisticas.dat";
    public static final String Partidas = "partidas";

    private File carpetaUsuarios;

    public GestorArchivos() {
        // carpeta personal de la persona
        File carpetaGeneral = new File(System.getProperty("user.home"), "FlowFreeDatos");
        carpetaUsuarios = new File(carpetaGeneral, "usuarios");

        
        if (carpetaUsuarios.exists() == false) {
            carpetaUsuarios.mkdirs();
        }
    }

    /** Devuelve la carpeta de un usuario  */
    public File carpetaDe(String nombreUsuario) {
        return new File(carpetaUsuarios, nombreUsuario);
    }

    /** Revisa si ya hay un usuario con ese nombre  */
    public boolean existeUsuario(String nombreUsuario) {
        File[] carpetas = carpetaUsuarios.listFiles();
        if (carpetas == null) {
            return false;
        }
        for (File carpeta : carpetas) {
            if (carpeta.isDirectory() && carpeta.getName().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    /** Crea la carpeta del usuario con su subcarpeta de partidas. */
    public void crearCarpetaUsuario(String nombreUsuario) throws IOException {
        File partidas = new File(carpetaDe(nombreUsuario), Partidas);
        partidas.mkdirs();
        if (partidas.exists() == false) {
            throw new IOException("No se pudo crear la carpeta del usuario.");
        }
    }

    /** Guarda un objeto en un archivo */
    public void guardar(File archivo, Serializable objeto) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo));
        salida.writeObject(objeto);
        salida.close();
    }

    /** Lee un objeto de un archivo binario. */
    public Object leer(File archivo) throws IOException {
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo));
        try {
            return entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("El archivo " + archivo.getName() + " está dañado.");
        } finally {
            entrada.close();
        }
    }

    
     //FUNCIÓN RECU
     // Borra un archivo o una carpeta completa con todo lo que tiene adentro.
     
    public void borrarRecursivo(File archivo) {
        if (archivo.isDirectory()) {
            File[] contenido = archivo.listFiles();
            if (contenido == null) {
                contenido = new File[0];
            }
            for (File hijo : contenido) {
                borrarRecursivo(hijo);
            }
        }
        archivo.delete();
    }
}