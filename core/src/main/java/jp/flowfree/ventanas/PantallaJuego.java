package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import java.io.IOException;
import jp.flowfree.Main;
import jp.flowfree.enums.TipoEvento;
import jp.flowfree.hilos.EventoJuego;
import jp.flowfree.hilos.HiloLogica;
import jp.flowfree.hilos.HiloTemporizador;
import jp.flowfree.hilos.AvisosJuego;
import jp.flowfree.juego.JuegoFlowFree;
import jp.flowfree.juego.Nivel;
import jp.flowfree.modelo.Partida;


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

    private Partida partida;
    private Label textoPuntaje;
    private Label textoVidas;
    private int segundosJugados = 0;

    // Menú de pausa 
    private Table capaPausa;
    private Label textoPausaInfo;

   
    private HiloLogica hiloLogica;
    private HiloTemporizador hiloTemporizador;

    public PantallaJuego(Main juego, Partida partida, Nivel nivel) {
        super(juego);
        this.partida = partida;
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
        textoPuntaje = agregarDato(marcador, String.valueOf(partida.getPuntajeTotal()), "Puntaje", skin);
        textoVidas = agregarDato(marcador, String.valueOf(partida.getVidas()), "Vidas", skin);
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

       
        // Deshacer
        botonDeshacer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                if (terminado) {
                    return;
                }
                hiloLogica.enviar(new EventoJuego(TipoEvento.DESHACER, 0, 0));
            }
        });

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

        crearMenuPausa(skin);

        // Se crean y arrancan los hilos
        hiloLogica = new HiloLogica(juegoFlow, this);
        hiloTemporizador = new HiloTemporizador(nivel.getTiempoLimite(), this);
        hiloLogica.start();
        hiloTemporizador.start();
    }

    
    private void crearMenuPausa(Skin skin) {
        capaPausa = new Table();
        capaPausa.setFillParent(true);
        capaPausa.setBackground(skin.newDrawable("white", 0, 0, 0, 0.55f));   // negro al 55%
        capaPausa.setTouchable(Touchable.enabled);   // se traga los clics: no se puede tocar el juego de atrás

        Table panel = new Table();
        panel.setBackground(skin.newDrawable("white", 0.16f, 0.16f, 0.21f, 1));
        panel.pad(20);

        Label titulo = new Label("Pausa", skin);
        titulo.setFontScale(1.5f);
        panel.add(titulo).padBottom(5).row();

        textoPausaInfo = new Label("", skin);
        textoPausaInfo.setColor(0.7f, 0.7f, 0.7f, 1);
        panel.add(textoPausaInfo).padBottom(20).row();

        TextButton botonContinuar = new TextButton("Continuar", skin);
        TextButton botonSalir = new TextButton("Salir al mapa", skin);
        panel.add(botonContinuar).width(220).height(40).padBottom(10).row();
        panel.add(botonSalir).width(220).height(40);

        capaPausa.add(panel);
        capaPausa.setVisible(false);
        escenario.addActor(capaPausa);

        botonContinuar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                cerrarPausa();
            }
        });

        botonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                confirmar("Salir al mapa", "Si sales del nivel perderás una vida.\n¿Quieres salir?", new Runnable() {
                    @Override
                    public void run() {
                        salirAlMapa();
                    }
                });
            }
        });
    }

    private void mostrarPausa() {
        pausadoPorJugador = true;
        hiloTemporizador.pausar();
        hiloLogica.pausar();
        juegoFlow.setOculto(true);

        String tiempo = "Tiempo ";
        if (juegoFlow.getNivel().tieneTiempoLimite()) {
            tiempo = "Tiempo restante ";
        }
        textoPausaInfo.setText("Nivel " + juegoFlow.getNivel().getNumero() + "  ·  " + tiempo + textoTiempo.getText());
        capaPausa.setVisible(true);
        capaPausa.toFront();   // que quede encima de todo lo demás
    }

    private void cerrarPausa() {
        capaPausa.setVisible(false);
        pausadoPorJugador = false;
        juegoFlow.setOculto(false);
        hiloTemporizador.reanudar();
        hiloLogica.reanudar();
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

    // Avisos de los hilos 

    @Override
    public void alCambiarTablero() {
        actualizarMarcador();
    }

    @Override
    public void alGanar() {
        ganarNivel();
    }

    @Override
    public void alLlegarLimiteFallos() {
        perderNivel("Llegaste al límite de fallos.");
    }

    @Override
    public void alPasarSegundo(int segundos) {
        segundosJugados = segundos;
        textoTiempo.setText(textoReloj(segundos));
    }

    @Override
    public void alAgotarseTiempo() {
        perderNivel("Se acabó el tiempo.");
    }

   
    // Fin del nivel 

    private void ganarNivel() {
        if (terminar() == false) {
            return;
        }
        int nivel = juegoFlow.getNivel().getNumero();
        int puntaje = calcularPuntaje();
        partida.completarNivel(nivel, puntaje);   
        guardarPartida();
        avisar("¡Nivel completado!", "Puntaje del nivel: " + puntaje, new Runnable() {
            @Override
            public void run() {
                juego.regresar();
            }
        });
    }

    private void perderNivel(String motivo) {
        if (terminar() == false) {
            return;
        }
        boolean perdioTodas = partida.perderVida();
        guardarPartida();
        if (perdioTodas) {
            avisar("Sin vidas", motivo + "\nPerdiste todas tus vidas.\nLa partida vuelve al nivel 1.", new Runnable() {
                @Override
                public void run() {
                    juego.regresar();
                }
            });
            return;
        }
        avisar("Perdiste una vida", motivo + "\nTe quedan " + partida.getVidas() + " vidas.\nVamos a repetir el nivel.", new Runnable() {
            @Override
            public void run() {
                repetirNivel();
            }
        });
    }

    // Salir te resta una vida
    private void salirAlMapa() {
        if (terminar() == false) {
            return;
        }
        boolean perdioTodas = partida.perderVida();
        guardarPartida();
        if (perdioTodas) {
            avisar("Sin vidas", "Perdiste todas tus vidas.\nLa partida vuelve al nivel 1.", new Runnable() {
                @Override
                public void run() {
                    juego.regresar();
                }
            });
            return;
        }
        juego.regresar();
    }

    // Marca el nivel como terminado
   
    private boolean terminar() {
        if (terminado) {
            return false;
        }
        terminado = true;
        detenerHilos();
        partida.sumarTiempo(segundosJugados);
        return true;
    }

    private int calcularPuntaje() {
        Nivel nivel = juegoFlow.getNivel();
        int bonoTiempo;
        if (nivel.tieneTiempoLimite()) {
            bonoTiempo = 2 * Math.max(0, nivel.getTiempoLimite() - segundosJugados);
        } else {
            bonoTiempo = Math.max(0, 60 - segundosJugados);
        }
        int castigo = 10 * (juegoFlow.getFallos() / 3);
        return Math.max(0, 100 + bonoTiempo - castigo);
    }

    private void guardarPartida() {
        try {
            juego.getGestionUsuarios().guardarPartida(partida);
        } catch (IOException e) {
            mensaje.setText("No se pudo guardar la partida.");
        }
        textoPuntaje.setText(String.valueOf(partida.getPuntajeTotal()));
        textoVidas.setText(String.valueOf(partida.getVidas()));
    }

   
    private void repetirNivel() {
        Nivel nivel = juegoFlow.getNivel();
        juego.regresar();
        juego.AbrirPantalla(new PantallaJuego(juego, partida, nivel));
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
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        juegoFlow.actualizar(delta);

        escenario.getViewport().apply();
        pincel.setProjectionMatrix(escenario.getViewport().getCamera().combined);
        juegoFlow.dibujar(pincel);

        escenario.act(delta);
        escenario.draw();
    }

    @Override
    public void dispose() {
        detenerHilos();   
        super.dispose();
        pincel.dispose();
    }
}
