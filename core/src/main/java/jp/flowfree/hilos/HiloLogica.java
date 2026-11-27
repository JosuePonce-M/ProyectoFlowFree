package jp.flowfree.hilos;

import com.badlogic.gdx.Gdx;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import jp.flowfree.enums.TipoAviso;
import jp.flowfree.enums.TipoEvento;
import jp.flowfree.juego.JuegoFlowFree;
import jp.flowfree.juego.Nivel;

/**
 * 
 * y aplica las reglas del juego: avanzar, cortar, fallos y victoria.
 */
public class HiloLogica extends HiloJuego {

   
    private final BlockingQueue<EventoJuego> cola = new LinkedBlockingQueue<>();
    private final JuegoFlowFree juego;
    private final AvisosJuego avisos;

    public HiloLogica(JuegoFlowFree juego, AvisosJuego avisos) {
        super("Logica");
        this.juego = juego;
        this.avisos = avisos;
    }

 
    public void enviar(EventoJuego evento) {
        cola.offer(evento);
    }

    @Override
    protected void procesar() throws InterruptedException {
        EventoJuego evento = cola.take();   
        aplicarEvento(evento);
    }

    private void aplicarEvento(EventoJuego evento) {
        switch (evento.getTipo()) {
            case PRESIONAR:
                juego.empezarTrazo(evento.getFila(), evento.getColumna());
                break;
            case MOVER:
                juego.moverA(evento.getFila(), evento.getColumna());
                break;
            case SOLTAR:
                juego.terminarTrazo();
                break;
            default:
                juego.reiniciarTablero();
                break;
        }
        avisar(TipoAviso.TABLERO);

        if (evento.getTipo() == TipoEvento.SOLTAR) {
            comprobarResultado();
        }
    }

    private void comprobarResultado() {
        if (juego.verificarVictoria()) {
            avisar(TipoAviso.VICTORIA);
            detener();
            return;
        }
        Nivel nivel = juego.getNivel();
        if (nivel.tieneLimiteFallos()) {
            if (juego.getFallos() >= nivel.getFallosPermitidos()) {
                avisar(TipoAviso.LIMITE_FALLOS);
                detener();
            }
        }
    }

    // Manda el aviso al hilo principal
    private void avisar(final TipoAviso aviso) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (aviso == TipoAviso.TABLERO) {
                    avisos.alCambiarTablero();
                } else if (aviso == TipoAviso.VICTORIA) {
                    avisos.alGanar();
                } else {
                    avisos.alLlegarLimiteFallos();
                }
            }
        });
    }
}
