package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import jp.flowfree.Main;
import jp.flowfree.juego.JuegoFlowFree;
import jp.flowfree.juego.Nivel;


public class PantallaJuego extends Pantalla {

    // Dónde va el tablero dentro de la ventana de 450x800
    private static final float TABLERO_X = 20;
    private static final float TABLERO_Y = 210;
    private static final float TABLERO_LADO = 410;

    private JuegoFlowFree juegoFlow;
    private ShapeRenderer pincel;  
    private Label mensaje;

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
        agregarDato(marcador, fallos, "Fallos", skin);
        agregarDato(marcador, "0", "Movim.", skin);
        agregarDato(marcador, "0%", "Lleno", skin);
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
        botonReiniciar.addListener(proximamente);

       // pausa
        botonPausa.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });
    }

    /** Agrega al marcador un dato */
    private void agregarDato(Table marcador, String valor, String nombre, Skin skin) {
        Table dato = new Table();
        dato.add(new Label(valor, skin)).row();
        Label etiqueta = new Label(nombre, skin);
        etiqueta.setColor(0.6f, 0.6f, 0.6f, 1);
        dato.add(etiqueta);
        marcador.add(dato).expandX();
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
