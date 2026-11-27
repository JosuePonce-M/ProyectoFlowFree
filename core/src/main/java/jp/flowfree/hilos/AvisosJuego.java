package jp.flowfree.hilos;


public interface AvisosJuego {

    void alCambiarTablero();

    void alGanar();

    void alLlegarLimiteFallos();

    void alPasarSegundo(int segundos);

    void alAgotarseTiempo();
}
