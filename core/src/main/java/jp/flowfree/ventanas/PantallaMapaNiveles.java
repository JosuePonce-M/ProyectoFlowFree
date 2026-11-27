package jp.flowfree.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import jp.flowfree.Main;
import jp.flowfree.enums.ColorPunto;
import jp.flowfree.enums.EstadoNivel;
import jp.flowfree.juego.Nivel;
import jp.flowfree.juego.Nivel1;
import jp.flowfree.juego.Nivel2;
import jp.flowfree.juego.Nivel3;
import jp.flowfree.juego.Nivel4;
import jp.flowfree.juego.Nivel5;
import jp.flowfree.modelo.Partida;
import jp.flowfree.modelo.Usuario;

public class PantallaMapaNiveles extends Pantalla {

    private static final int TOTAL = Partida.TOTAL_NIVELES;

    // Centro de cada círculo (posición 0 = nivel 1). En libGDX la Y crece hacia arriba
    private static final float[] CIRCULO_X = {125, 325, 125, 325, 225};
    private static final float[] CIRCULO_Y = {145, 245, 345, 445, 545};

    // Esquina donde dobla el camino entre el nivel i y el nivel i+1
    private static final float[] ESQUINA_X = {325, 125, 325, 225};
    private static final float[] ESQUINA_Y = {145, 245, 345, 445};

    private static final float RADIO = 38;
    private static final float GROSOR_CAMINO = 16;

    // Color de cada nivel, tomado de los mismos colores del juego
    private static final ColorPunto[] COLORES = {
        ColorPunto.ROJO, ColorPunto.AZUL, ColorPunto.VERDE, ColorPunto.AMARILLO, ColorPunto.MORADO
    };

    private static final Color FONDO_CIRCULO = Color.valueOf("2C2C3A");
    private static final Color CUADRICULA = Color.valueOf("262636");
    private static final Color PUNTEADO = Color.valueOf("3A3A4A");
    private static final Color CANDADO = Color.valueOf("9A9AAA");
    private static final Color VERDE_CHECK = Color.valueOf("3B6D11");
    private static final Color CORAZON_LLENO = Color.valueOf("E24B4A");
    private static final Color CORAZON_VACIO = Color.valueOf("555566");

    private Partida partida;
    private ShapeRenderer pincel;
    private Label[] numeros = new Label[TOTAL];
    private Label puntaje;
    private float tiempo = 0;   

    public PantallaMapaNiveles(Main juego, Partida partida) {
        super(juego);
        this.partida = partida;
        pincel = new ShapeRenderer();
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();
        Usuario usuario = juego.getGestionUsuarios().getUsuarioActual();

        // Arriba a la izquierda
        Table izquierda = new Table();
        izquierda.setFillParent(true);
        izquierda.top().left().pad(15);
        izquierda.add(new Label("Partida " + partida.getNumero(), skin));
        escenario.addActor(izquierda);

        // Arriba a la derecha
        Table derecha = new Table();
        derecha.setFillParent(true);
        derecha.top().right().pad(15);
        derecha.add(new Label(usuario.getNombreCompleto(), skin)).padRight(8);
        derecha.add(crearAvatarTemporal(usuario, skin)).size(36).row();
        puntaje = new Label("", skin);
        derecha.add(puntaje).colspan(2).right().padTop(14);
        escenario.addActor(derecha);

        // Título
        Label titulo = new Label("Niveles", skin);
        titulo.setFontScale(1.8f);
        titulo.setAlignment(Align.center);
        titulo.setBounds(0, 640, 450, 50);
        escenario.addActor(titulo);

        
        for (int i = 0; i < TOTAL; i++) {
            final int nivel = i + 1;

            Label numero = new Label(String.valueOf(nivel), skin);
            numero.setFontScale(1.8f);
            numero.setAlignment(Align.center);
            numero.setBounds(CIRCULO_X[i] - RADIO, CIRCULO_Y[i] - RADIO, RADIO * 2, RADIO * 2);
            numeros[i] = numero;
            escenario.addActor(numero);

            Actor zona = new Actor();
            zona.setBounds(CIRCULO_X[i] - RADIO, CIRCULO_Y[i] - RADIO, RADIO * 2, RADIO * 2);
            zona.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent evento, float x, float y) {
                    tocarNivel(nivel);
                }
            });
            escenario.addActor(zona);
        }

        
        Table abajo = new Table();
        abajo.setFillParent(true);
        abajo.bottom().right().pad(15);
        TextButton botonVolver = new TextButton("Volver", skin);
        abajo.add(botonVolver).width(110).height(40);
        escenario.addActor(abajo);

        botonVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });
    }

 
    @Override
    public void show() {
        super.show();
        actualizarTextos();
    }

    private void actualizarTextos() {
        puntaje.setText("Puntaje: " + partida.getPuntajeTotal());
        for (int i = 0; i < TOTAL; i++) {
            // El número solo se ve si el nivel no está bloqueado (si no, va el candado)
            numeros[i].setVisible(partida.estaBloqueado(i + 1) == false);
        }
    }

    private void tocarNivel(int nivel) {
        if (partida.estaBloqueado(nivel)) {
            avisar("Nivel bloqueado", "Completa el nivel anterior.", new Runnable() {
                @Override
                public void run() {
                }
            });
            return;
        }
        juego.AbrirPantalla(new PantallaJuego(juego, partida, crearNivel(nivel)));
    }

    private Nivel crearNivel(int nivel) {
        switch (nivel) {
            case 1:
                return new Nivel1();
            case 2:
                return new Nivel2();
            case 3:
                return new Nivel3();
            case 4:
                return new Nivel4();
            default:
                return new Nivel5();
        }
    }

    @Override
    public void render(float delta) {
        tiempo = tiempo + delta;
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        escenario.getViewport().apply();
        pincel.setProjectionMatrix(escenario.getViewport().getCamera().combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);   
        pincel.begin(ShapeRenderer.ShapeType.Filled);
        dibujarCuadricula();
        dibujarCaminos();
        for (int i = 0; i < TOTAL; i++) {
            dibujarCirculo(i);
        }
        dibujarVidas();
        pincel.end();

        // Los textos y botones van encima de los dibujos
        escenario.act(delta);
        escenario.draw();
    }

    private void dibujarCuadricula() {
        pincel.setColor(CUADRICULA);
        for (int x = 75; x <= 375; x = x + 50) {
            pincel.rectLine(x, 95, x, 620, 2);
        }
        for (int y = 95; y <= 620; y = y + 50) {
            pincel.rectLine(50, y, 400, y, 2);
        }
    }

   
    private void dibujarCaminos() {
        for (int i = 0; i < TOTAL - 1; i++) {
            float x1 = CIRCULO_X[i];
            float y1 = CIRCULO_Y[i];
            float xe = ESQUINA_X[i];
            float ye = ESQUINA_Y[i];
            float x2 = CIRCULO_X[i + 1];
            float y2 = CIRCULO_Y[i + 1];

            if (partida.getEstado(i + 1) == EstadoNivel.COMPLETADO) {
                // Nivel completado: el camino se pinta de su color
                pincel.setColor(COLORES[i].getColor());
                pincel.rectLine(x1, y1, xe, ye, GROSOR_CAMINO);
                pincel.rectLine(xe, ye, x2, y2, GROSOR_CAMINO);
                pincel.circle(xe, ye, GROSOR_CAMINO / 2);   
            } else {
                pincel.setColor(PUNTEADO);
                punteado(x1, y1, xe, ye);
                punteado(xe, ye, x2, y2);
            }
        }
    }

    
    private void punteado(float x1, float y1, float x2, float y2) {
        float largo = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        int cantidad = (int) (largo / 18);
        for (int k = 0; k <= cantidad; k++) {
            float t = k / (float) cantidad;
            pincel.circle(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t, 3);
        }
    }

    private void dibujarCirculo(int i) {
        float x = CIRCULO_X[i];
        float y = CIRCULO_Y[i];
        Color color = COLORES[i].getColor();
        EstadoNivel estado = partida.getEstado(i + 1);

        if (estado == EstadoNivel.COMPLETADO) {
            pincel.setColor(color);
            pincel.circle(x, y, RADIO, 48);
           
            pincel.setColor(Color.WHITE);
            pincel.circle(x + 27, y + 27, 12, 24);
            pincel.setColor(VERDE_CHECK);
            pincel.rectLine(x + 21, y + 28, x + 25, y + 23, 3);
            pincel.rectLine(x + 25, y + 23, x + 33, y + 31, 3);

        } else if (estado == EstadoNivel.DISPONIBLE) {
         
            float pulso = 50 + 6 * MathUtils.sin(tiempo * 3);
            pincel.setColor(color.r, color.g, color.b, 0.5f);
            anillo(x, y, pulso, 3);
            pincel.setColor(color);
            pincel.circle(x, y, RADIO, 48);
            pincel.setColor(Color.WHITE);
            anillo(x, y, RADIO, 4);

        } else {
            // Bloqueado
            pincel.setColor(FONDO_CIRCULO);
            pincel.circle(x, y, RADIO, 48);
            pincel.setColor(color);
            anillo(x, y, RADIO, 3);
            pincel.setColor(CANDADO);
            pincel.rect(x - 14, y - 14, 28, 20);
            arco(x, y + 6, 9, 4);
        }
    }

    
    private void anillo(float cx, float cy, float radio, float grosor) {
        int partes = 48;
        for (int k = 0; k < partes; k++) {
            float a1 = MathUtils.PI2 * k / partes;
            float a2 = MathUtils.PI2 * (k + 1) / partes;
            pincel.rectLine(cx + radio * MathUtils.cos(a1), cy + radio * MathUtils.sin(a1),
                    cx + radio * MathUtils.cos(a2), cy + radio * MathUtils.sin(a2), grosor);
        }
    }

 
    private void arco(float cx, float cy, float radio, float grosor) {
        int partes = 12;
        for (int k = 0; k < partes; k++) {
            float a1 = MathUtils.PI * k / partes;
            float a2 = MathUtils.PI * (k + 1) / partes;
            pincel.rectLine(cx + radio * MathUtils.cos(a1), cy + radio * MathUtils.sin(a1),
                    cx + radio * MathUtils.cos(a2), cy + radio * MathUtils.sin(a2), grosor);
        }
    }

    // Tres corazones
    private void dibujarVidas() {
        for (int k = 0; k < Partida.VIDAS_INICIALES; k++) {
            if (k < partida.getVidas()) {
                pincel.setColor(CORAZON_LLENO);
            } else {
                pincel.setColor(CORAZON_VACIO);
            }
            corazon(30 + k * 34, 735);
        }
    }

    
    private void corazon(float x, float y) {
        pincel.circle(x - 6, y, 7, 20);
        pincel.circle(x + 6, y, 7, 20);
        pincel.triangle(x - 13, y - 2, x + 13, y - 2, x, y - 16);
    }


    @Override
    public void dispose() {
        super.dispose();
        pincel.dispose();
    }
}
