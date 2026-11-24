package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.io.IOException;
import jp.flowfree.Main;
import jp.flowfree.datos.GestionUsuarios;
import jp.flowfree.modelo.Partida;
import jp.flowfree.modelo.Usuario;

public class PantallaPartidas extends Pantalla {

    public static final int MAX_PARTIDAS = 3;

    private Label contador;
    private Label mensaje;

    public PantallaPartidas(Main juego) {
        super(juego);
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();
        Usuario usuario = juego.getGestionUsuarios().getUsuarioActual();

        // Arriba a la derecha: nombre y avatar
        Table esquina = new Table();
        esquina.setFillParent(true);
        esquina.top().right().pad(15);
        esquina.add(new Label(usuario.getNombreCompleto(), skin)).padRight(8);
        esquina.add(crearAvatarTemporal(usuario, skin)).size(36);
        escenario.addActor(esquina);

        // Centro: título, los tres botones y el contador
        Table tabla = new Table();
        tabla.setFillParent(true);

        Label titulo = new Label("Partidas", skin);
        titulo.setFontScale(1.8f);
        tabla.add(titulo).padBottom(40).row();

        TextButton botonNueva = new TextButton("Nueva partida", skin);
        TextButton botonContinuar = new TextButton("Continuar partida", skin);
        TextButton botonEliminar = new TextButton("Eliminar partida", skin);
        tabla.add(botonNueva).width(300).height(44).padBottom(15).row();
        tabla.add(botonContinuar).width(300).height(44).padBottom(15).row();
        tabla.add(botonEliminar).width(300).height(44).padBottom(25).row();

        contador = new Label("", skin);
        contador.setColor(Color.LIGHT_GRAY);
        tabla.add(contador).row();

        mensaje = new Label("", skin);
        mensaje.setColor(Color.RED);
        tabla.add(mensaje).padTop(15).row();
        escenario.addActor(tabla);

        // Abajo a la derecha: Volver
        Table abajo = new Table();
        abajo.setFillParent(true);
        abajo.bottom().right().pad(15);
        TextButton botonVolver = new TextButton("Volver", skin);
        abajo.add(botonVolver).width(110).height(40);
        escenario.addActor(abajo);

        botonNueva.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                nuevaPartida();
            }
        });
        botonContinuar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                abrirLista(true);
            }
        });
        botonEliminar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                abrirLista(false);
            }
        });
        botonVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });
    }

    // Al volver de la lista (por ejemplo, después de eliminar) se actualiza el contador
    @Override
    public void show() {
        super.show();
        mensaje.setText("");
        try {
            contador.setText("Partidas guardadas: " + contarPartidas() + " de " + MAX_PARTIDAS);
        } catch (IOException e) {
            contador.setText("");
            mensaje.setText("No se pudieron leer las partidas.");
        }
    }

    private int contarPartidas() throws IOException {
        GestionUsuarios gestion = juego.getGestionUsuarios();
        int cantidad = 0;
        for (int numero = 1; numero <= MAX_PARTIDAS; numero++) {
            if (gestion.leerPartida(numero) == null) {
                continue;
            }
            cantidad++;
        }
        return cantidad;
    }

    // Devuelve el primer espacio libre (1, 2 o 3), o 0 si están los tres ocupados
    private int buscarEspacioLibre() throws IOException {
        GestionUsuarios gestion = juego.getGestionUsuarios();
        for (int numero = 1; numero <= MAX_PARTIDAS; numero++) {
            if (gestion.leerPartida(numero) == null) {
                return numero;
            }
        }
        return 0;
    }

    private void nuevaPartida() {
        try {
            int numero = buscarEspacioLibre();
            if (numero == 0) {
                confirmar("Partidas llenas",
                        "Ya tienes 3 partidas guardadas.\n¿Quieres eliminar una?", new Runnable() {
                    @Override
                    public void run() {
                        juego.AbrirPantalla(new PantallaListaPartidas(juego, false));
                    }
                });
                return;
            }
            Partida partida = new Partida(numero);
            juego.getGestionUsuarios().guardarPartida(partida);
            juego.AbrirPantalla(new PantallaMapaNiveles(juego, partida));
        } catch (IOException e) {
            mensaje.setText("No se pudo crear la partida.");
        }
    }

    // modoContinuar = true para Continuar, false para Eliminar
    private void abrirLista(boolean modoContinuar) {
        try {
            if (contarPartidas() == 0) {
                avisar("Sin partidas", "No tienes partidas guardadas.", new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                return;
            }
            juego.AbrirPantalla(new PantallaListaPartidas(juego, modoContinuar));
        } catch (IOException e) {
            mensaje.setText("No se pudieron leer las partidas.");
        }
    }
}
