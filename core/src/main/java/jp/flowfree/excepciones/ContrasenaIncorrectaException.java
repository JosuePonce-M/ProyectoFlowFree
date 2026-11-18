package jp.flowfree.excepciones;

public class ContrasenaIncorrectaException extends FlowFreeException {

    public ContrasenaIncorrectaException() {
        super("Contraseña incorrecta.");
    }
}