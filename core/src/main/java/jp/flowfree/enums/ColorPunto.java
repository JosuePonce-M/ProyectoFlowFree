package jp.flowfree.enums;

import com.badlogic.gdx.graphics.Color;

public enum ColorPunto {

    ROJO("E24B4A"),
    AZUL("378ADD"),
    VERDE("639922"),
    AMARILLO("EF9F27"),
    MORADO("7F77DD"),
    ROSADO("D4537E"),
    VERDE_AGUA("1D9E75"),
    NARANJA("D85A30"),
    AZUL_OSCURO("0C447C");

    private final String codigo;

    ColorPunto(String codigo) {
        this.codigo = codigo;
    }

    public Color getColor() {
        return Color.valueOf(codigo);
    }
}