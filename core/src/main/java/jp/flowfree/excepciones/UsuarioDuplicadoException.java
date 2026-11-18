package jp.flowfree.excepciones;

public class UsuarioDuplicadoException extends FlowFreeException {

    public UsuarioDuplicadoException() {
        super("Ese nombre de usuario ya está en uso.");
    }
}