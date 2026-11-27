package jp.flowfree.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import jp.flowfree.enums.ColorPunto;
import jp.flowfree.enums.TipoCasilla;


public class JuegoFlowFree extends Juego {

    private static final Color COLOR_FONDO = new Color(0.07f, 0.07f, 0.10f, 1);
    private static final Color COLOR_LINEAS = new Color(0.23f, 0.23f, 0.29f, 1);
    private static final Color COLOR_PARED = new Color(0.37f, 0.37f, 0.35f, 1);

    private Nivel nivel;
    private int tamano;

  
    private TipoCasilla[][] tipos;
    private ColorPunto[][] colores;

    
    private HashMap<ColorPunto, ArrayList<int[]>> caminos = new HashMap<>();
   
    private ColorPunto[][] ocupadas;

   
    private ColorPunto colorActivo = null;
    private boolean huboCambio = false;
    private HashSet<ColorPunto> cortadosEnEsteArrastre = new HashSet<>();

    // Marcador
    private int movimientos = 0;
    private int fallos = 0;

    // En pausa el tablero se dibuja "apagado": solo la cuadrícula, sin pistas
    private boolean oculto = false;

    // Pila para Deshacer: cada elemento es una copia de todas las líneas del tablero
    private Stack<HashMap<ColorPunto, ArrayList<int[]>>> pilaDeshacer = new Stack<>();
    private HashMap<ColorPunto, ArrayList<int[]>> fotoAntesDelTrazo = null;

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
        ocupadas = new ColorPunto[tamano][tamano];

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
            caminos.put(pareja.getColor(), new ArrayList<int[]>());
        }
    }

    // Trazar con el mouse 

   
    public synchronized void empezarTrazo(int fila, int columna) {
        if (dentro(fila, columna) == false) {
            return;
        }
        huboCambio = false;
        cortadosEnEsteArrastre.clear();
        fotoAntesDelTrazo = copiarCaminos();

        if (tipos[fila][columna] == TipoCasilla.PUNTO) {
           
            colorActivo = colores[fila][columna];
            ArrayList<int[]> camino = caminos.get(colorActivo);
            camino.clear();
            camino.add(new int[]{fila, columna});
            huboCambio = true;
            recalcularOcupadas();
            return;
        }
        if (ocupadas[fila][columna] == null) {
            return;   
        }
        
        colorActivo = ocupadas[fila][columna];
        ArrayList<int[]> camino = caminos.get(colorActivo);
        int posicion = indiceEn(camino, fila, columna);
        if (posicion < camino.size() - 1) {
            recortar(camino, posicion + 1);
            huboCambio = true;
        }
        recalcularOcupadas();
    }

  
    public synchronized void moverA(int fila, int columna) {
        if (colorActivo == null) {
            return;
        }
        ArrayList<int[]> camino = caminos.get(colorActivo);
        int pasosMaximos = tamano * tamano;
        while (pasosMaximos > 0) {
            pasosMaximos--;
            int[] ultima = camino.get(camino.size() - 1);
            int df = fila - ultima[0];
            int dc = columna - ultima[1];
            if (df == 0 && dc == 0) {
                return;   
            }
            int pasoFila = Integer.signum(df);
            int pasoColumna = Integer.signum(dc);

            boolean avanzo = false;
            if (Math.abs(dc) >= Math.abs(df)) {
                //  horizontal
                if (pasoColumna == 0 || paso(ultima[0], ultima[1] + pasoColumna) == false) {
                    if (pasoFila == 0) {
                        return;
                    }
                    avanzo = paso(ultima[0] + pasoFila, ultima[1]);
                } else {
                    avanzo = true;
                }
            } else {
                //  vertical
                if (pasoFila == 0 || paso(ultima[0] + pasoFila, ultima[1]) == false) {
                    if (pasoColumna == 0) {
                        return;
                    }
                    avanzo = paso(ultima[0], ultima[1] + pasoColumna);
                } else {
                    avanzo = true;
                }
            }
            if (avanzo == false) {
                return;   
            }
        }
    }


    public synchronized void terminarTrazo() {
        if (colorActivo == null) {
            return;
        }
        if (huboCambio) {
            movimientos++;
            pilaDeshacer.push(fotoAntesDelTrazo);
        }
        fotoAntesDelTrazo = null;
        colorActivo = null;
    }

  
    private boolean paso(int fila, int columna) {
        if (dentro(fila, columna) == false) {
            return false;
        }
        if (tipos[fila][columna] == TipoCasilla.PARED) {
            return false;
        }
        ArrayList<int[]> camino = caminos.get(colorActivo);

      
        int posicion = indiceEn(camino, fila, columna);
        if (posicion >= 0) {
            if (posicion < camino.size() - 1) {
                recortar(camino, posicion + 1);
                huboCambio = true;
                recalcularOcupadas();
            }
            return true;
        }

        
        if (estaConectado(colorActivo)) {
            return false;
        }

        if (tipos[fila][columna] == TipoCasilla.PUNTO) {
            if (colores[fila][columna] == colorActivo) {
                camino.add(new int[]{fila, columna});   // llegó al otro punto
                huboCambio = true;
                recalcularOcupadas();
                return true;
            }
            return false;    
        }

        
        ColorPunto otro = ocupadas[fila][columna];
        if (otro == null) {
           
        } else {
            ArrayList<int[]> caminoOtro = caminos.get(otro);
            recortar(caminoOtro, indiceEn(caminoOtro, fila, columna));
            if (cortadosEnEsteArrastre.contains(otro) == false) {
                cortadosEnEsteArrastre.add(otro);
                fallos++;
            }
        }

        camino.add(new int[]{fila, columna});
        huboCambio = true;
        recalcularOcupadas();
        return true;
    }

    
    public synchronized void reiniciarTablero() {
        if (tableroVacio() == false) {
            pilaDeshacer.push(copiarCaminos());
        }
        for (ArrayList<int[]> camino : caminos.values()) {
            camino.clear();
        }
        colorActivo = null;
        recalcularOcupadas();
    }

    /** Saca la última foto de la pila y deja el tablero como estaba. Devuelve false si no hay nada que deshacer */
    public synchronized boolean deshacer() {
        if (pilaDeshacer.isEmpty()) {
            return false;
        }
        HashMap<ColorPunto, ArrayList<int[]>> anterior = pilaDeshacer.pop();
        for (ColorPunto color : caminos.keySet()) {
            ArrayList<int[]> camino = caminos.get(color);
            camino.clear();
            camino.addAll(anterior.get(color));
        }
        colorActivo = null;
        recalcularOcupadas();
        return true;
    }

    //  Ayudantes 

    // Copia de todas las líneas (copia nueva de cada casilla, para que no se modifique después)
    private HashMap<ColorPunto, ArrayList<int[]>> copiarCaminos() {
        HashMap<ColorPunto, ArrayList<int[]>> copia = new HashMap<>();
        for (ColorPunto color : caminos.keySet()) {
            ArrayList<int[]> lista = new ArrayList<>();
            for (int[] casilla : caminos.get(color)) {
                lista.add(new int[]{casilla[0], casilla[1]});
            }
            copia.put(color, lista);
        }
        return copia;
    }

    private boolean tableroVacio() {
        for (ArrayList<int[]> camino : caminos.values()) {
            if (camino.isEmpty() == false) {
                return false;
            }
        }
        return true;
    }

    private boolean dentro(int fila, int columna) {
        return fila >= 0 && fila < tamano && columna >= 0 && columna < tamano;
    }

    // Posición de la casilla dentro del camino
    private int indiceEn(ArrayList<int[]> camino, int fila, int columna) {
        for (int i = 0; i < camino.size(); i++) {
            if (camino.get(i)[0] == fila && camino.get(i)[1] == columna) {
                return i;
            }
        }
        return -1;
    }

    
    private void recortar(ArrayList<int[]> camino, int desde) {
        while (camino.size() > desde) {
            camino.remove(camino.size() - 1);
        }
    }

    private void recalcularOcupadas() {
        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                ocupadas[fila][columna] = null;
            }
        }
        for (ColorPunto color : caminos.keySet()) {
            for (int[] casilla : caminos.get(color)) {
                ocupadas[casilla[0]][casilla[1]] = color;
            }
        }
    }

    /** si esta conectada una linea*/
    public synchronized boolean estaConectado(ColorPunto color) {
        ArrayList<int[]> camino = caminos.get(color);
        if (camino.size() < 2) {
            return false;
        }
        int[] ultima = camino.get(camino.size() - 1);
        return tipos[ultima[0]][ultima[1]] == TipoCasilla.PUNTO;
    }

    /** Porcentaje de casillas */
    public synchronized int getPorcentajeLleno() {
        int libres = 0;
        int llenas = 0;
        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                if (tipos[fila][columna] == TipoCasilla.PARED) {
                    continue;
                }
                libres++;
                ColorPunto color = ocupadas[fila][columna];
                if (color == null) {
                    continue;
                }
                if (caminos.get(color).size() >= 2) {
                    llenas++;
                }
            }
        }
        return llenas * 100 / libres;
    }

    // Victoria recu

    @Override
    public synchronized boolean verificarVictoria() {
        if (getPorcentajeLleno() < 100) {
            return false;
        }
        for (Pareja pareja : nivel.getParejas()) {
            boolean[][] visitadas = new boolean[tamano][tamano];
            boolean unidos = hayCamino(pareja.getFila1(), pareja.getColumna1(),
                    pareja.getFila2(), pareja.getColumna2(), pareja.getColor(), visitadas);
            if (unidos == false) {
                return false;
            }
        }
        return true;
    }

    
    // FUNCIÓN RECURSIVA de la lógica del juego.
    
    private boolean hayCamino(int fila, int columna, int filaDestino, int columnaDestino,
            ColorPunto color, boolean[][] visitadas) {
        if (dentro(fila, columna) == false) {
            return false;
        }
        if (visitadas[fila][columna]) {
            return false;
        }
        if (ocupadas[fila][columna] == color) {
            
        } else {
            return false;
        }
        if (fila == filaDestino && columna == columnaDestino) {
            return true;  
        }
        visitadas[fila][columna] = true;
        return hayCamino(fila + 1, columna, filaDestino, columnaDestino, color, visitadas)
                || hayCamino(fila - 1, columna, filaDestino, columnaDestino, color, visitadas)
                || hayCamino(fila, columna + 1, filaDestino, columnaDestino, color, visitadas)
                || hayCamino(fila, columna - 1, filaDestino, columnaDestino, color, visitadas);
    }

    // Dibujo 

    @Override
    public void actualizar(float delta) {
    }

    public void setArea(float x, float y, float lado) {
        this.x = x;
        this.y = y;
        this.lado = lado;
    }

    public synchronized void setOculto(boolean oculto) {
        this.oculto = oculto;
    }

    @Override
    public synchronized void dibujar(ShapeRenderer pincel) {
        float celda = lado / tamano;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        pincel.begin(ShapeRenderer.ShapeType.Filled);
        // Fondo y paredes
        pincel.setColor(COLOR_FONDO);
        pincel.rect(x, y, lado, lado);
        if (oculto == false) {
            for (int fila = 0; fila < tamano; fila++) {
                for (int columna = 0; columna < tamano; columna++) {
                    if (tipos[fila][columna] == TipoCasilla.PARED) {
                        pincel.setColor(COLOR_PARED);
                        pincel.rect(xDe(columna, celda), yDe(fila, celda), celda, celda);
                    } else if (ocupadas[fila][columna] == null) {
                  
                    } else {
                        // Casilla con línea: un tono suave de su color, como en el juego original
                        Color c = ocupadas[fila][columna].getColor();
                        pincel.setColor(c.r, c.g, c.b, 0.18f);
                        pincel.rect(xDe(columna, celda), yDe(fila, celda), celda, celda);
                    }
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

        if (oculto) {
            return;
        }

        pincel.begin(ShapeRenderer.ShapeType.Filled);
        // Líneas de colores: de centro a centro
        float grosor = celda * 0.3f;
        for (ColorPunto color : caminos.keySet()) {
            ArrayList<int[]> camino = caminos.get(color);
            pincel.setColor(color.getColor());
            for (int i = 0; i < camino.size(); i++) {
                float cx = centroX(camino.get(i)[1], celda);
                float cy = centroY(camino.get(i)[0], celda);
                pincel.circle(cx, cy, grosor / 2, 20);
                if (i > 0) {
                    float px = centroX(camino.get(i - 1)[1], celda);
                    float py = centroY(camino.get(i - 1)[0], celda);
                    pincel.rectLine(px, py, cx, cy, grosor);
                }
            }
        }
        // Puntos de colores 
        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {
                if (tipos[fila][columna] == TipoCasilla.PUNTO) {
                    pincel.setColor(colores[fila][columna].getColor());
                    pincel.circle(centroX(columna, celda), centroY(fila, celda), celda * 0.34f, 40);
                }
            }
        }
        pincel.end();
    }

    private float xDe(int columna, float celda) {
        return x + columna * celda;
    }

    private float yDe(int fila, float celda) {
        return y + lado - (fila + 1) * celda;
    }

    private float centroX(int columna, float celda) {
        return xDe(columna, celda) + celda / 2;
    }

    private float centroY(int fila, float celda) {
        return yDe(fila, celda) + celda / 2;
    }

    // Convierte una posición del mouse en fila y columna
    public int filaEn(float posY) {
        float celda = lado / tamano;
        return tamano - 1 - (int) Math.floor((posY - y) / celda);
    }

    public int columnaEn(float posX) {
        float celda = lado / tamano;
        return (int) Math.floor((posX - x) / celda);
    }

    public Nivel getNivel() {
        return nivel;
    }

    public synchronized int getMovimientos() {
        return movimientos;
    }

    public synchronized int getFallos() {
        return fallos;
    }
}
