package jp.flowfree.excepciones;

public class UsuarioInvalidoException extends FlowFreeException {

    public UsuarioInvalidoException() {
        super("El usuario no puede tener espacios ni símbolos.");
    }
}
