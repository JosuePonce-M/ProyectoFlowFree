package jp.flowfree.juego;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class JuegoFlowFree extends Juego {

    private static final Color COLOR_FONDO = new Color(0.07f, 0.07f, 0.10f, 1);
    private static final Color COLOR_LINEAS = new Color(0.23f, 0.23f, 0.29f, 1);
    private static final Color COLOR_PARED = new Color(0.37f, 0.37f, 0.35f, 1);

    private Nivel nivel;
    private int tamano;

   
    private TipoCasilla[][] tipos;
    private ColorPunto[][] colores; 

    
    private float x;
    private float y;
    private float lado;

    public JuegoFlowFree(Nivel nivel) {
        this.nivel = nivel;
        this.tamano = nivel.getTamano();
        iniciar();
    }

   
    @Override
    public void iniciar() {
        tipos = new TipoCasilla[tamano][tamano];
        colores = new ColorPunto[tamano][tamano];

        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                tipos[fila][columna] = TipoCasilla.VACIA;
            }
        }

        for (int[] pared : nivel.getParedes()) {
            tipos[pared[0]][pared[1]] = TipoCasilla.PARED;
        }

        for (Pareja pareja : nivel.getParejas()) {
            tipos[pareja.getFila1()][pareja.getColumna1()] = TipoCasilla.PUNTO;
            colores[pareja.getFila1()][pareja.getColumna1()] = pareja.getColor();
            tipos[pareja.getFila2()][pareja.getColumna2()] = TipoCasilla.PUNTO;
            colores[pareja.getFila2()][pareja.getColumna2()] = pareja.getColor();
        }
    }

    @Override
    public void actualizar(float delta) {
      
    }

  
    public void setArea(float x, float y, float lado) {
        this.x = x;
        this.y = y;
        this.lado = lado;
    }

    @Override
    public void dibujar(ShapeRenderer pincel) {
        float celda = lado / tamano;

        //  Fondo y paredes 
        pincel.begin(ShapeRenderer.ShapeType.Filled);
        pincel.setColor(COLOR_FONDO);
        pincel.rect(x, y, lado, lado);
        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                if (tipos[fila][columna] == TipoCasilla.PARED) {
                    pincel.setColor(COLOR_PARED);
                    pincel.rect(xDe(columna, celda), yDe(fila, celda), celda, celda);
                }
            }
        }
        pincel.end();

        // Líneas de la cuadrícula
        pincel.begin(ShapeRenderer.ShapeType.Line);
        pincel.setColor(COLOR_LINEAS);
        for (int i = 0; i <= tamano; i++) {
            pincel.line(x, y + i * celda, x + lado, y + i * celda);   // horizontales
            pincel.line(x + i * celda, y, x + i * celda, y + lado);   // verticales
        }
        pincel.end();

        //  Puntos de colores
        pincel.begin(ShapeRenderer.ShapeType.Filled);
        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                if (tipos[fila][columna] == TipoCasilla.PUNTO) {
                    pincel.setColor(colores[fila][columna].getColor());
                    float centroX = xDe(columna, celda) + celda / 2;
                    float centroY = yDe(fila, celda) + celda / 2;
                    pincel.circle(centroX, centroY, celda * 0.34f, 40);
                }
            }
        }
        pincel.end();
    }

    // Posición x 
    private float xDe(int columna, float celda) {
        return x + columna * celda;
    }

    // Posición y 
    private float yDe(int fila, float celda) {
        return y + lado - (fila + 1) * celda;
    }

    @Override
    public boolean verificarVictoria() {
        return false;
    }

    public Nivel getNivel() {
        return nivel;
    }
}
