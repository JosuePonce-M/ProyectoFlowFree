package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import jp.flowfree.Main;
import jp.flowfree.juego.JuegoFlowFree;
import jp.flowfree.juego.Nivel;


public class PantallaJuego extends Pantalla {

    
    private static final float TABLERO_X = 20;
    private static final float TABLERO_Y = 210;
    private static final float TABLERO_LADO = 410;

    private JuegoFlowFree juegoFlow;
    private ShapeRenderer pincel;  
    private Label mensaje;
    private Label textoFallos;
    private Label textoMovimientos;
    private Label textoLleno;
    private boolean terminado = false;   

    public PantallaJuego(Main juego, Nivel nivel) {
        super(juego);
        juegoFlow = new JuegoFlowFree(nivel);
        juegoFlow.setArea(TABLERO_X, TABLERO_Y, TABLERO_LADO);
        pincel = new ShapeRenderer();
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();
        Nivel nivel = juegoFlow.getNivel();
        String nombre = juego.getGestionUsuarios().getUsuarioActual().getNombreCompleto();

      // Parte de arriba
        Table arriba = new Table();
        arriba.setFillParent(true);
        arriba.top().pad(15);
        arriba.add(new Label("Nivel " + nivel.getNumero(), skin)).expandX().left();
        arriba.add(new Label("00:00", skin)).expandX();
        arriba.add(new Label(nombre, skin)).expandX().right();
        arriba.row();

        // Marcador
        String fallos = "0";
        if (nivel.tieneLimiteFallos()) {
            fallos = "0 / " + nivel.getFallosPermitidos();
        }
        Table marcador = new Table();
        agregarDato(marcador, "0", "Puntaje", skin);
        agregarDato(marcador, "3", "Vidas", skin);
        textoFallos = agregarDato(marcador, fallos, "Fallos", skin);
        textoMovimientos = agregarDato(marcador, "0", "Movim.", skin);
        textoLleno = agregarDato(marcador, "0%", "Lleno", skin);
        arriba.add(marcador).colspan(3).expandX().fillX().padTop(20);
        escenario.addActor(arriba);

        //  Abajo: botones 
        Table abajo = new Table();
        abajo.setFillParent(true);
        abajo.bottom().pad(20);

        mensaje = new Label("", skin);
        abajo.add(mensaje).colspan(3).padBottom(15).row();

        TextButton botonDeshacer = new TextButton("Deshacer", skin);
        TextButton botonReiniciar = new TextButton("Reiniciar", skin);
        TextButton botonPausa = new TextButton("Pausa", skin);
        abajo.add(botonDeshacer).width(125).height(40).padRight(10);
        abajo.add(botonReiniciar).width(125).height(40).padRight(10);
        abajo.add(botonPausa).width(125).height(40);
        escenario.addActor(abajo);

       
        ChangeListener proximamente = new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                mensaje.setText(".");
            }
        };
        botonDeshacer.addListener(proximamente);

        // Reiniciar
        botonReiniciar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                if (terminado) {
                    return;
                }
                juegoFlow.reiniciarTablero();
                actualizarMarcador();
            }
        });

        crearZonaTablero();

       // pausa
        botonPausa.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });
    }

    
    private void crearZonaTablero() {
        Actor zona = new Actor();
        zona.setBounds(TABLERO_X, TABLERO_Y, TABLERO_LADO, TABLERO_LADO);
        zona.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent evento, float x, float y, int puntero, int boton) {
                if (terminado) {
                    return false;
                }
                juegoFlow.empezarTrazo(filaDe(y), columnaDe(x));
                actualizarMarcador();
                return true;   
            }

            @Override
            public void touchDragged(InputEvent evento, float x, float y, int puntero) {
                juegoFlow.moverA(filaDe(y), columnaDe(x));
                actualizarMarcador();
            }

            @Override
            public void touchUp(InputEvent evento, float x, float y, int puntero, int boton) {
                juegoFlow.terminarTrazo();
                actualizarMarcador();
                revisarFinDelNivel();
            }
        });
        escenario.addActor(zona);
    }

    
    private int filaDe(float y) {
        return juegoFlow.filaEn(TABLERO_Y + y);
    }

    private int columnaDe(float x) {
        return juegoFlow.columnaEn(TABLERO_X + x);
    }

    private void actualizarMarcador() {
        Nivel nivel = juegoFlow.getNivel();
        String fallos = String.valueOf(juegoFlow.getFallos());
        if (nivel.tieneLimiteFallos()) {
            fallos = fallos + " / " + nivel.getFallosPermitidos();
        }
        textoFallos.setText(fallos);
        textoMovimientos.setText(String.valueOf(juegoFlow.getMovimientos()));
        textoLleno.setText(juegoFlow.getPorcentajeLleno() + "%");
    }

   
    private void revisarFinDelNivel() {
        if (juegoFlow.verificarVictoria()) {
            terminado = true;
            mensaje.setText("¡Nivel completado!");
            return;
        }
        Nivel nivel = juegoFlow.getNivel();
        if (nivel.tieneLimiteFallos()) {
            if (juegoFlow.getFallos() >= nivel.getFallosPermitidos()) {
                terminado = true;
                mensaje.setText("Llegaste al límite de fallos.");
            }
        }
    }

    
    private Label agregarDato(Table marcador, String valor, String nombre, Skin skin) {
        Table dato = new Table();
        Label textoValor = new Label(valor, skin);
        dato.add(textoValor).row();
        Label etiqueta = new Label(nombre, skin);
        etiqueta.setColor(0.6f, 0.6f, 0.6f, 1);
        dato.add(etiqueta);
        marcador.add(dato).expandX();
        return textoValor;
    }

    @Override
    public void render(float delta) {
        super.render(delta); 
        juegoFlow.actualizar(delta);

     
        pincel.setProjectionMatrix(escenario.getViewport().getCamera().combined);
        juegoFlow.dibujar(pincel);
    }

    @Override
    public void dispose() {
        super.dispose();
        pincel.dispose();
    }
}
