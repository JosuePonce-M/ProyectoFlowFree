package jp.flowfree.util;


public class ValidarContraseña {

    public static boolean tieneLongitud(String contrasena) {
        return contrasena.length() >= 8;
    }

    public static boolean tieneMayuscula(String contrasena) {
        for (int i = 0; i < contrasena.length(); i++) {
            if (Character.isUpperCase(contrasena.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean tieneMinuscula(String contrasena) {
        for (int i = 0; i < contrasena.length(); i++) {
            if (Character.isLowerCase(contrasena.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean tieneNumero(String contrasena) {
        for (int i = 0; i < contrasena.length(); i++) {
            if (Character.isDigit(contrasena.charAt(i))) {
                return true;
            }
        }
        return false;
    }

  
    public static boolean tieneSimbolo(String contrasena) {
        for (int i = 0; i < contrasena.length(); i++) {
            char c = contrasena.charAt(i);
            if (Character.isLetterOrDigit(c) == false && Character.isWhitespace(c) == false) {
                return true;
            }
        }
        return false;
    }

    
    public static boolean esValida(String contrasena) {
        return tieneLongitud(contrasena)
                && tieneMayuscula(contrasena)
                && tieneMinuscula(contrasena)
                && tieneNumero(contrasena)
                && tieneSimbolo(contrasena);
    }
}
