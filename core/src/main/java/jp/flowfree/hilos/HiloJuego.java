package jp.flowfree.hilos;


public abstract class HiloJuego extends Thread {

    private volatile boolean activo = true;   
    private boolean pausado = false;

    public HiloJuego(String nombre) {
        super(nombre);
        setDaemon(true);  
    }

    @Override
    public void run() {
        while (activo) {
            try {
                esperarSiEstaPausado();
                if (activo == false) {
                    break;
                }
                procesar();
            } catch (InterruptedException e) {
               
            }
        }
    }

    // Mientras esté pausado
    private synchronized void esperarSiEstaPausado() throws InterruptedException {
        while (pausado && activo) {
            wait();
        }
    }

   
    protected abstract void procesar() throws InterruptedException;

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void reanudar() {
        pausado = false;
        notifyAll();  
    }

    // Detiene el hilo 
    public void detener() {
        activo = false;
        synchronized (this) {
            notifyAll();
        }
        interrupt();   
    }

    public boolean estaActivo() {
        return activo;
    }
}
