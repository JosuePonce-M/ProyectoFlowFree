package jp.flowfree.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Protege las contraseñas para que nunca se guarden tal como el usuario las escribió.
 */
public class SeguridadContra {

    /** Crea una "sal": un texto al azar distinto para cada usuario. */
    public static String generarSal() {
        SecureRandom azar = new SecureRandom();
        byte[] bytes = new byte[16];
        azar.nextBytes(bytes);
        return aTexto(bytes);
    }

    /** Convierte la contraseña (junto con la sal) en un código SHA-256 que no se puede revertir. */
    public static String protegerContrasena(String contrasena, String sal) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(sal.getBytes(StandardCharsets.UTF_8));
            byte[] codigo = sha256.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            return aTexto(codigo);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no está disponible", e);
        }
    }

    /** Pasa los bytes a texto en hexadecimal (números y letras de la a a la f). */
    private static String aTexto(byte[] bytes) {
        StringBuilder texto = new StringBuilder();
        for (byte b : bytes) {
            texto.append(String.format("%02x", b));
        }
        return texto.toString();
    }
}