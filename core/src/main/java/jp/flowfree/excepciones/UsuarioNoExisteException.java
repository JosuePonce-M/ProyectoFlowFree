package jp.flowfree.excepciones;

public class UsuarioNoExisteException extends FlowFreeException {

    public UsuarioNoExisteException() {
        super("Ese usuario no existe.");
    }
}
