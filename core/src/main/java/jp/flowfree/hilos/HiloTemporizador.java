package jp.flowfree.hilos;

import com.badlogic.gdx.Gdx;

/** Lleva el tiempo del nivel y avisa cada segundo y cuando se agota */
public class HiloTemporizador extends HiloJuego {

    private final int limite;          // 0 = el nivel no tiene tiempo límite
    private final AvisosJuego avisos;
    private int segundos = 0;
    private int milisegundos = 0;

    public HiloTemporizador(int limite, AvisosJuego avisos) {
        super("Temporizador");
        this.limite = limite;
        this.avisos = avisos;
    }

   
    @Override
    protected void procesar() throws InterruptedException {
        Thread.sleep(100);
        milisegundos = milisegundos + 100;
        if (milisegundos < 1000) {
            return;
        }
        milisegundos = 0;
        segundos++;

        final int actual = segundos;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                avisos.alPasarSegundo(actual);
            }
        });

        if (limite > 0 && segundos >= limite) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    avisos.alAgotarseTiempo();
                }
            });
            detener();
        }
    }

    public int getSegundos() {
        return segundos;
    }
}
