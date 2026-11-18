package jp.flowfree.excepciones;

public class CamposVaciosException extends FlowFreeException {

    public CamposVaciosException() {
        super("Completa todos los campos.");
    }
}
