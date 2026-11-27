package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import jp.flowfree.Main;
import jp.flowfree.enums.TipoEvento;
import jp.flowfree.hilos.EventoJuego;
import jp.flowfree.hilos.HiloLogica;
import jp.flowfree.hilos.HiloTemporizador;
import jp.flowfree.hilos.AvisosJuego;
import jp.flowfree.juego.JuegoFlowFree;
import jp.flowfree.juego.Nivel;


public class PantallaJuego extends Pantalla implements AvisosJuego {

    
    private static final float TABLERO_X = 20;
    private static final float TABLERO_Y = 210;
    private static final float TABLERO_LADO = 410;

    private JuegoFlowFree juegoFlow;
    private ShapeRenderer pincel;  
    private Label mensaje;
    private Label textoFallos;
    private Label textoMovimientos;
    private Label textoLleno;
    private Label textoTiempo;
    private boolean terminado = false;
    private boolean pausadoPorJugador = false;

   
    private HiloLogica hiloLogica;
    private HiloTemporizador hiloTemporizador;

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
        textoTiempo = new Label(textoReloj(0), skin);
        arriba.add(textoTiempo).expandX();
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
                hiloLogica.enviar(new EventoJuego(TipoEvento.REINICIAR, 0, 0));
            }
        });

        crearZonaTablero();

       // pausa
        botonPausa.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                if (terminado) {
                    return;
                }
                mostrarPausa();
            }
        });

        // Se crean y arrancan los hilos
        hiloLogica = new HiloLogica(juegoFlow, this);
        hiloTemporizador = new HiloTemporizador(nivel.getTiempoLimite(), this);
        hiloLogica.start();
        hiloTemporizador.start();
    }

    // Menú de pausa
    private void mostrarPausa() {
        pausadoPorJugador = true;
        hiloTemporizador.pausar();
        hiloLogica.pausar();

        Dialog dialogo = new Dialog("Pausa", juego.getSkin()) {
            @Override
            protected void result(Object respuesta) {
                if (Boolean.TRUE.equals(respuesta)) {
                    pausadoPorJugador = false;
                    hiloTemporizador.reanudar();
                    hiloLogica.reanudar();
                } else {
                    juego.regresar();  
                }
            }
        };
        dialogo.getContentTable().pad(15);
        dialogo.getButtonTable().pad(0, 15, 15, 15);
        dialogo.text("Juego en pausa");
        dialogo.getButtonTable().defaults().width(120).height(32).padLeft(10).padRight(10);
        dialogo.button("Continuar", true);
        dialogo.button("Salir al mapa", false);
        dialogo.show(escenario);
    }

    
    private void crearZonaTablero() {
        Actor zona = new Actor();
        zona.setBounds(TABLERO_X, TABLERO_Y, TABLERO_LADO, TABLERO_LADO);
        zona.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent evento, float x, float y, int puntero, int boton) {
                if (terminado || pausadoPorJugador) {
                    return false;
                }
                // El mouse ya no cambia el tablero: solo deja el evento en la cola
                hiloLogica.enviar(new EventoJuego(TipoEvento.PRESIONAR, filaDe(y), columnaDe(x)));
                return true;
            }

            @Override
            public void touchDragged(InputEvent evento, float x, float y, int puntero) {
                hiloLogica.enviar(new EventoJuego(TipoEvento.MOVER, filaDe(y), columnaDe(x)));
            }

            @Override
            public void touchUp(InputEvent evento, float x, float y, int puntero, int boton) {
                hiloLogica.enviar(new EventoJuego(TipoEvento.SOLTAR, 0, 0));
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

    // ===== Avisos de los hilos (siempre llegan al hilo principal) =====

    @Override
    public void alCambiarTablero() {
        actualizarMarcador();
    }

    @Override
    public void alGanar() {
        terminarNivel("¡Nivel completado!");
    }

    @Override
    public void alLlegarLimiteFallos() {
        terminarNivel("Llegaste al límite de fallos.");
    }

    @Override
    public void alPasarSegundo(int segundos) {
        textoTiempo.setText(textoReloj(segundos));
    }

    @Override
    public void alAgotarseTiempo() {
        terminarNivel("Se acabó el tiempo.");
    }

   
    private void terminarNivel(String texto) {
        if (terminado) {
            return;
        }
        terminado = true;
        mensaje.setText(texto);
        detenerHilos();
    }

 
    private String textoReloj(int segundosPasados) {
        int mostrar = segundosPasados;
        Nivel nivel = juegoFlow.getNivel();
        if (nivel.tieneTiempoLimite()) {
            mostrar = Math.max(0, nivel.getTiempoLimite() - segundosPasados);
        }
        return String.format("%02d:%02d", mostrar / 60, mostrar % 60);
    }

    private void detenerHilos() {
        if (hiloLogica == null) {
            return;
        }
        hiloLogica.detener();
        hiloTemporizador.detener();
    }

  
    @Override
    public void pause() {
        if (hiloLogica == null) {
            return;
        }
        hiloTemporizador.pausar();
        hiloLogica.pausar();
    }

    @Override
    public void resume() {
        if (hiloLogica == null || pausadoPorJugador) {
            return;
        }
        hiloTemporizador.reanudar();
        hiloLogica.reanudar();
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
        detenerHilos();   
        super.dispose();
        pincel.dispose();
    }
}
